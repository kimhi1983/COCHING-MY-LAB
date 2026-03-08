#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
로그 설정 및 관리 유틸리티
"""

import os
import logging
from logging.handlers import TimedRotatingFileHandler
from datetime import datetime, timedelta
import glob

def setup_logging(log_level="INFO", log_dir="logs", max_days=30):
    """
    로깅 설정 (데일리 롤링)
    
    Args:
        log_level: 로그 레벨 (DEBUG, INFO, WARNING, ERROR, CRITICAL)
        log_dir: 로그 디렉토리
        max_days: 로그 보관 기간 (일)
    """
    # logs 디렉토리 생성
    if not os.path.exists(log_dir):
        os.makedirs(log_dir)
    
    # 로거 설정
    logger = logging.getLogger()
    logger.setLevel(getattr(logging, log_level.upper()))
    
    # 기존 핸들러 제거 (중복 방지)
    for handler in logger.handlers[:]:
        logger.removeHandler(handler)
    
    # 파일 핸들러 설정 (데일리 롤링)
    file_handler = TimedRotatingFileHandler(
        filename=os.path.join(log_dir, "cosmetic_api.log"),
        when="midnight",  # 자정에 롤링
        interval=1,       # 1일마다
        backupCount=max_days,   # 지정된 일수만큼 보관
        encoding="utf-8"
    )
    file_handler.suffix = "%Y-%m-%d"  # 파일명에 날짜 추가
    
    # 콘솔 핸들러 설정
    console_handler = logging.StreamHandler()
    
    # 포맷터 설정
    formatter = logging.Formatter(
        '%(asctime)s - %(name)s - %(levelname)s - %(message)s',
        datefmt='%Y-%m-%d %H:%M:%S'
    )
    file_handler.setFormatter(formatter)
    console_handler.setFormatter(formatter)
    
    # 핸들러 추가
    logger.addHandler(file_handler)
    logger.addHandler(console_handler)
    
    return logger

def cleanup_old_logs(log_dir="logs", max_days=30):
    """
    오래된 로그 파일 정리
    
    Args:
        log_dir: 로그 디렉토리
        max_days: 보관 기간 (일)
    """
    if not os.path.exists(log_dir):
        return
    
    cutoff_date = datetime.now() - timedelta(days=max_days)
    
    # 로그 파일 패턴 매칭
    log_pattern = os.path.join(log_dir, "cosmetic_api.log.*")
    log_files = glob.glob(log_pattern)
    
    deleted_count = 0
    for log_file in log_files:
        try:
            # 파일 수정 시간 확인
            file_time = datetime.fromtimestamp(os.path.getmtime(log_file))
            if file_time < cutoff_date:
                os.remove(log_file)
                deleted_count += 1
                print(f"🗑️ 오래된 로그 파일 삭제: {log_file}")
        except Exception as e:
            print(f"❌ 로그 파일 삭제 실패: {log_file} - {e}")
    
    if deleted_count > 0:
        print(f"✅ {deleted_count}개의 오래된 로그 파일을 정리했습니다.")

def get_log_stats(log_dir="logs"):
    """
    로그 파일 통계 정보
    
    Args:
        log_dir: 로그 디렉토리
        
    Returns:
        dict: 로그 통계 정보
    """
    if not os.path.exists(log_dir):
        return {"error": "로그 디렉토리가 존재하지 않습니다."}
    
    log_pattern = os.path.join(log_dir, "cosmetic_api.log*")
    log_files = glob.glob(log_pattern)
    
    stats = {
        "total_files": len(log_files),
        "total_size_mb": 0,
        "files": []
    }
    
    for log_file in log_files:
        try:
            file_size = os.path.getsize(log_file)
            file_time = datetime.fromtimestamp(os.path.getmtime(log_file))
            
            stats["total_size_mb"] += file_size
            stats["files"].append({
                "filename": os.path.basename(log_file),
                "size_mb": round(file_size / (1024 * 1024), 2),
                "modified": file_time.strftime('%Y-%m-%d %H:%M:%S')
            })
        except Exception as e:
            print(f"❌ 로그 파일 정보 조회 실패: {log_file} - {e}")
    
    stats["total_size_mb"] = round(stats["total_size_mb"] / (1024 * 1024), 2)
    return stats

def main():
    """로그 관리 메인 함수"""
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
    
    print("\n🧹 오래된 로그 파일 정리 중...")
    cleanup_old_logs()

if __name__ == "__main__":
    main()
