#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
로그 파일 관리 스크립트
"""

import os
import sys
import argparse
from log_config import get_log_stats, cleanup_old_logs, setup_logging

def show_logs():
    """로그 파일 목록 및 통계 표시"""
    print("📊 로그 파일 통계")
    print("=" * 50)
    
    stats = get_log_stats()
    if "error" in stats:
        print(f"❌ {stats['error']}")
        return
    
    print(f"📁 총 로그 파일 수: {stats['total_files']}개")
    print(f"💾 총 크기: {stats['total_size_mb']}MB")
    print("\n📋 파일 목록:")
    
    for file_info in stats["files"]:
        print(f"   - {file_info['filename']}: {file_info['size_mb']}MB ({file_info['modified']})")

def clean_logs(days=30):
    """오래된 로그 파일 정리"""
    print(f"🧹 {days}일 이전 로그 파일 정리 중...")
    cleanup_old_logs(max_days=days)
    print("✅ 로그 정리 완료!")

def tail_logs(lines=50):
    """최신 로그 파일의 마지막 N줄 표시"""
    log_file = "logs/cosmetic_api.log"
    
    if not os.path.exists(log_file):
        print(f"❌ 로그 파일이 존재하지 않습니다: {log_file}")
        return
    
    try:
        with open(log_file, 'r', encoding='utf-8') as f:
            all_lines = f.readlines()
            last_lines = all_lines[-lines:] if len(all_lines) > lines else all_lines
            
            print(f"📄 최신 로그 {len(last_lines)}줄:")
            print("-" * 50)
            for line in last_lines:
                print(line.rstrip())
    except Exception as e:
        print(f"❌ 로그 파일 읽기 실패: {e}")

def main():
    """메인 함수"""
    parser = argparse.ArgumentParser(description="로그 파일 관리 도구")
    parser.add_argument("command", choices=["show", "clean", "tail"], 
                       help="실행할 명령어 (show: 통계, clean: 정리, tail: 최신 로그)")
    parser.add_argument("--days", type=int, default=30, 
                       help="로그 보관 기간 (일, clean 명령어용)")
    parser.add_argument("--lines", type=int, default=50, 
                       help="표시할 로그 라인 수 (tail 명령어용)")
    
    args = parser.parse_args()
    
    if args.command == "show":
        show_logs()
    elif args.command == "clean":
        clean_logs(args.days)
    elif args.command == "tail":
        tail_logs(args.lines)

if __name__ == "__main__":
    main()
