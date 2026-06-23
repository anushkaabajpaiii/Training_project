import urllib.request
import re

def fetch(url):
    req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0'})
    return urllib.request.urlopen(req, timeout=30).read().decode('utf-8', errors='ignore')

for topic in ['https://github.com/topics', 'https://github.com/topics/python', 'https://github.com/topics/javascript']:
    print('URL=', topic)
    data = fetch(topic)
    print('len', len(data))
    print('topic links count', data.count('/topics/python'), data.count('/topics/javascript'), data.count('/topics/java'))
    # print sample anchors around python/javascript
    for m in re.finditer(r'href=[\'\"](/topics/[^\'\"]+)[\'\"]', data):
        if m.group(1) in ['/topics/python', '/topics/javascript', '/topics/java']:
            print('found', m.group(1), 'snippet', data[max(0,m.start()-80):m.end()+80].replace('\n',' '))
    # print first repo link candidates
    print('repo anchors occurrences', len(re.findall(r'href=[\'\"](/[^\'\"]+/[^\'\"]+)[\'\"]', data)))
    # search for repo card anchor patterns
    for pattern in [r'<a[^>]+href=[\'\"](/[^\'\"]+/[^\'\"]+)[\'\"][^>]*>\s*<svg', r'<h3[^>]*>\s*<a[^>]+href=[\'\"](/[^\'\"]+/[^\'\"]+)[\'\"]', r'<article[^>]*>(.*?)</article>']:
        found = re.findall(pattern, data, re.IGNORECASE)
        print('pattern', pattern, 'count', len(found))
    print('\n-----\n')
