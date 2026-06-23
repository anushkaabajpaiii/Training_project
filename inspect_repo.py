import urllib.request
import re

url = 'https://github.com/CyC2018/CS-Notes'
req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0'})
with urllib.request.urlopen(req, timeout=30) as resp:
    data = resp.read().decode('utf-8', errors='ignore')

patterns = [
    r'<h1[^>]*>.*?</h1>',
    r'<strong[^>]*>.*?</strong>',
    r'<span[^>]+itemprop=["\']programmingLanguage["\'][^>]*>.*?</span>',
    r'<p[^>]+class=["\'][^"\']*f4[^"\']*["\'][^>]*>.*?</p>',
    r'href=["\'](/[^"\']+/[^"\']+)["\']',
]

print('len', len(data))
print('itemprop name count', data.count('itemprop="name"'), data.count("itemprop='name'"))
print('programmingLanguage count', data.count('itemprop="programmingLanguage"'), data.count("itemprop='programmingLanguage'"))
print('strong itemprop count', data.count('strong itemprop'))
print('h1 count', len(re.findall(patterns[0], data, re.S)))
print('strong count', len(re.findall(patterns[1], data, re.S)))
print('lang span count', len(re.findall(patterns[2], data, re.S)))
print('desc p count', len(re.findall(patterns[3], data, re.S)))
print('sample h1', re.findall(patterns[0], data, re.S)[:5])
print('sample lang span', re.findall(patterns[2], data, re.S)[:5])
print('sample desc p', re.findall(patterns[3], data, re.S)[:5])
print('repo href count', len(re.findall(patterns[4], data)))
print('repo href sample', [h for h in re.findall(patterns[4], data) if h.count('/') == 2][:20])
