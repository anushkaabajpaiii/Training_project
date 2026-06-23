import urllib.request
import re

url = 'https://github.com/topics'
req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0'})
with urllib.request.urlopen(req, timeout=30) as resp:
    data = resp.read().decode('utf-8', errors='ignore')

print('java_count=', data.count('/topics/java'))
print('python_count=', data.count('/topics/python'))
print('java_href_exact=', 'href="/topics/java"' in data)
print('python_href_exact=', 'href="/topics/python"' in data)
print('java_link_snippet:')
idx = data.find('/topics/java')
if idx != -1:
    print(data[max(0, idx-200):idx+200].replace('\n', ' '))

# look for any topics hrefs and print first few unique values
hrefs = re.findall(r'href=[\'\"]([^\'\"]*?/topics[^\'\"]*)[\'\"]', data)
print('topic hrefs sample=', hrefs[:50])
print('unique topic hrefs=', sorted(set([h for h in hrefs if '/topics/' in h]))[:20])
