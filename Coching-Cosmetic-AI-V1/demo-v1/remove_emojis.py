import re

def remove_emojis(text):
    """텍스트에서 이모지를 제거합니다."""
    # 이모지 패턴 (유니코드 범위)
    emoji_pattern = re.compile(
        "["
        "\U0001F600-\U0001F64F"  # emoticons
        "\U0001F300-\U0001F5FF"  # symbols & pictographs
        "\U0001F680-\U0001F6FF"  # transport & map symbols
        "\U0001F1E0-\U0001F1FF"  # flags (iOS)
        "\U00002702-\U000027B0"  # dingbats
        "\U000024C2-\U0001F251"  # enclosed characters
        "]+", flags=re.UNICODE)
    
    return emoji_pattern.sub('', text)

# api_client_new_format.py 파일 읽기
with open('api_client_new_format.py', 'r', encoding='utf-8') as f:
    content = f.read()

# 이모지 제거
cleaned_content = remove_emojis(content)

# 파일에 다시 쓰기
with open('api_client_new_format.py', 'w', encoding='utf-8') as f:
    f.write(cleaned_content)

print("이모지 제거 완료!")
