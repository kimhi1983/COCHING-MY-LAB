#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
   API  
"""

import requests
import json
import time
from typing import List, Dict, Any

# API  
API_BASE_URL = "http://localhost:8000"

def test_health():
    """  """
    print("  ...")
    try:
        response = requests.get(f"{API_BASE_URL}/health")
        if response.status_code == 200:
            data = response.json()
            print(f"  : {data['status']}")
            print(f"  : {'' if data['elasticsearch_connected'] else ''}")
            print(f"   : {data['active_requests']}")
            print(f"   : {data['max_workers']}")
            return True
        else:
            print(f"   : {response.status_code}")
            return False
    except Exception as e:
        print(f"   : {e}")
        return False

def test_formula_new_format():
    """    """
    print("\n🧪     ...")
    
    #     (   )
    request_data = {
        "ingredients": [
            {
                "id": 2700,
                "repName": "정제수",
                "repNameEn": "Water"
            },
            {
                "id": 4445,
                "repName": "글리세린",
                "repNameEn": "Glycerin"
            },
            {
                "id": 1880,
                "repName": "알부틴",
                "repNameEn": "Arbutin"
            },
            {
                "id": 300,
                "repName": "아스코르빅애시드",
                "repNameEn": "Ascorbic Acid"
            },
            {
                "id": 3288,
                "repName": "토코페롤",
                "repNameEn": "Tocopherol"
            }
        ],
        "formulation": "미백세럼",
        "direction": "집중 미백 및 피부톤 개선",
        "model_name": "gpt-5-mini",
        "temperature": 0.3
    }
    
    try:
        print(f"  :")
        print(json.dumps(request_data, ensure_ascii=False, indent=2))
        
        start_time = time.time()
        response = requests.post(
            f"{API_BASE_URL}/formula",
            json=request_data,
            headers={"Content-Type": "application/json"}
        )
        end_time = time.time()
        
        if response.status_code == 200:
            data = response.json()
            print(f"\n   !")
            print(f"  :")
            print(f"   -  : {data['success']}")
            print(f"   - : {data['message']}")
            print(f"   -  : {data['processing_time']}")
            print(f"   -  : {'' if data['elasticsearch_connected'] else ''}")
            print(f"   -   : {data['ingredients_found']}/{data['total_ingredients']}")
            print(f"   -  ID: {data['request_id']}")
            
            #    
            if data.get('request_data'):
                request_data = data['request_data']
                print(f"\n  :")
                print(f"   - : {request_data.get('formulation', 'N/A')}")
                print(f"   - : {request_data.get('direction', 'N/A')}")
                print(f"   -  : {len(request_data.get('ingredients', []))}")
                
                #   
                if request_data.get('ingredients'):
                    print(f"   -  :")
                    for i, ingredient in enumerate(request_data['ingredients'][:5], 1):  #  5 
                        print(f"     {i}. {ingredient.get('rep_name', 'N/A')} ({ingredient.get('rep_name_en', 'N/A')})")
                        if ingredient.get('id'):
                            print(f"        ID: {ingredient['id']}")
            
            if data.get('data'):
                formula_data = data['data']
                print(f"\n  :")
                print(f"   - : {formula_data.get('product_name', 'N/A')}")
                print(f"   - : {formula_data.get('formulation_type', 'N/A')}")
                print(f"   - : {formula_data.get('development_direction', 'N/A')}")
                print(f"   -   : {len(formula_data.get('ingredients', []))}")
                
                #   
                if formula_data.get('ingredients'):
                    print(f"\n🧪  :")
                    for i, ingredient in enumerate(formula_data['ingredients'][:5], 1):  #  5 
                        print(f"   {i}. {ingredient.get('name', 'N/A')} - {ingredient.get('percentage', 'N/A')}%")
                        if ingredient.get('es_id'):
                            print(f"      ( ID: {ingredient['es_id']})")
            
            return True
        else:
            print(f"   : {response.status_code}")
            print(f"  : {response.text}")
            return False
            
    except Exception as e:
        print(f"   : {e}")
        return False

def test_formula_without_elasticsearch():
    """     """
    print("\n🧪    ...")
    
    #     
    request_data = {
        "ingredients": [
            {
                "id": None,
                "repName": "존재하지않는성분1",
                "repNameEn": "NonExistentIngredient1"
            },
            {
                "id": None,
                "repName": "존재하지않는성분2",
                "repNameEn": "NonExistentIngredient2"
            }
        ],
        "formulation": "테스트세럼",
        "direction": "테스트용 처방",
        "model_name": "gpt-5-mini",
        "temperature": 0.3
    }
    
    try:
        print(f"  :")
        print(json.dumps(request_data, ensure_ascii=False, indent=2))
        
        start_time = time.time()
        response = requests.post(
            f"{API_BASE_URL}/formula",
            json=request_data,
            headers={"Content-Type": "application/json"}
        )
        end_time = time.time()
        
        if response.status_code == 200:
            data = response.json()
            print(f"\n   !")
            print(f"  :")
            print(f"   -  : {data['success']}")
            print(f"   -   : {data['ingredients_found']}/{data['total_ingredients']}")
            print(f"   -  : {data['processing_time']}")
            
            return True
        else:
            print(f"   : {response.status_code}")
            return False
            
    except Exception as e:
        print(f"   : {e}")
        return False

def test_mixed_ingredients():
    """      """
    print("\n🧪    ( ,  )...")
    
    request_data = {
        "ingredients": [
            {
                "id": 2700,
                "repName": "정제수",
                "repNameEn": "Water"
            },
            {
                "id": None,
                "repName": "존재하지않는성분",
                "repNameEn": "NonExistentIngredient"
            },
            {
                "id": 1880,
                "repName": "알부틴",
                "repNameEn": "Arbutin"
            }
        ],
        "formulation": "혼합테스트세럼",
        "direction": "혼합 성분 테스트"
    }
    
    try:
        print(f"  :")
        print(json.dumps(request_data, ensure_ascii=False, indent=2))
        
        start_time = time.time()
        response = requests.post(
            f"{API_BASE_URL}/formula",
            json=request_data,
            headers={"Content-Type": "application/json"}
        )
        end_time = time.time()
        
        if response.status_code == 200:
            data = response.json()
            print(f"\n   !")
            print(f"  :")
            print(f"   -  : {data['success']}")
            print(f"   -   : {data['ingredients_found']}/{data['total_ingredients']}")
            print(f"   -  : {data['processing_time']}")
            
            return True
        else:
            print(f"   : {response.status_code}")
            return False
            
    except Exception as e:
        print(f"   : {e}")
        return False

def test_priority_search():
    """   (ID → rep_name_en → rep_name)"""
    print("\n🧪   ...")
    
    request_data = {
        "ingredients": [
            {
                "id": 2700,  # ID로 검색 (1순위)
                "repName": "정제수",
                "repNameEn": "Water"
            },
            {
                "id": None,  # ID 없음, repNameEn으로 검색 (2순위)
                "repName": "알부틴",
                "repNameEn": "Arbutin"
            },
            {
                "id": None,  # ID 없음, repNameEn도 없으면 repName으로 검색 (3순위)
                "repName": "토코페롤",
                "repNameEn": "Tocopherol"
            }
        ],
        "formulation": "우선순위테스트세럼",
        "direction": "우선순위 검색 테스트",
        "model_name": "gpt-5-mini",
        "temperature": 0.3
    }
    
    try:
        print(f"  :")
        print(json.dumps(request_data, ensure_ascii=False, indent=2))
        
        start_time = time.time()
        response = requests.post(
            f"{API_BASE_URL}/formula",
            json=request_data,
            headers={"Content-Type": "application/json"}
        )
        end_time = time.time()
        
        if response.status_code == 200:
            data = response.json()
            print(f"\n   !")
            print(f"  :")
            print(f"   -  : {data['success']}")
            print(f"   -   : {data['ingredients_found']}/{data['total_ingredients']}")
            print(f"   -  : {data['processing_time']}")
            
            return True
        else:
            print(f"   : {response.status_code}")
            return False
            
    except Exception as e:
        print(f"   : {e}")
        return False

def test_different_models():
    """   """
    print("\n🧪    ...")
    
    # GPT-4o   (  )
    request_data = {
        "ingredients": [
            {
                "id": 2700,
                "repName": "정제수",
                "repNameEn": "Water"
            },
            {
                "id": 4445,
                "repName": "글리세린",
                "repNameEn": "Glycerin"
            }
        ],
        "formulation": "보습크림",
        "direction": "강력한 보습 효과",
        "model_name": "gpt-4o",
        "temperature": 0.7
    }
    
    try:
        print(f"   (GPT-4o,  0.7):")
        print(json.dumps(request_data, ensure_ascii=False, indent=2))
        
        response = requests.post(f"{API_BASE_URL}/formula", json=request_data)
        
        if response.status_code == 200:
            result = response.json()
            print(f"  !")
            print(f"  : {result['processing_time']}")
            print(f"🤖  : {request_data['model_name']}")
            print(f"  : {request_data['temperature']}")
            print(f"  : {result['elasticsearch_connected']}")
            print(f"   : {result['ingredients_found']}/{result['total_ingredients']}")
            
            if result['success'] and result['data']:
                print(f"\n  :")
                print(f": {result['data']['title']}")
                print(f": {result['data']['description']}")
                print(f": {result['data']['formula']}")
        else:
            print(f"  : {response.status_code}")
            print(f": {response.text}")
            
    except Exception as e:
        print(f"    : {e}")

def main():
    """  """
    print("   API  ")
    print("=" * 50)
    
    # 1.  
    if not test_health():
        print("   .   .")
        return
    
    # 2.     
    test_formula_new_format()
    
    # 3.    
    test_formula_without_elasticsearch()
    
    # 4.   
    test_mixed_ingredients()
    
    # 5.   
    test_priority_search()
    
    # 6.    
    test_different_models()
    
    print("\n  !")

if __name__ == "__main__":
    main()
