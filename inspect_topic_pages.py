import urllib.request
import re

def fetch(url):
    req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0'})
    return urllib.request.urlopen(req, timeout=30).read().decode('utf-8', errors='ignore')

for url in [
    'https://github.com/topics',
    'https://github.com/topics/python',
    'https://github.com/topics/java'
]:
    print('='*80)
    print('URL:', url)
    data = fetch(url)
    print('len', len(data))
    print('contains java exact href', 'href="/topics/java"' in data)
    print('contains python exact href', 'href="/topics/python"' in data)
    print('contains javascript exact href', 'href="/topics/javascript"' in data)
    print('count /topics/java', data.count('/topics/java'))
    print('count /topics/javascript', data.count('/topics/javascript'))
    # find anchor tags for topics
    topics = re.findall(r'<a[^>]+href=[\'\"](/topics/[^\'\"]+)[\'\"][^>]*>(.*?)</a>', data, re.DOTALL)
    print('topics anchors total:', len(topics))
    samples = [t for t in topics if t[0] in ['/topics/java','/topics/python','/topics/javascript']]
    print('topic samples', samples[:20])
    # repository links heuristics
    repo_hrefs = re.findall(r'<a[^>]+href=[\'\"](/[^\'\"]+/[^\'\"]+)[\'\"][^>]*>', data)
    print('repo hrefs sample count', len(repo_hrefs))
    sample_unique = sorted(set([h for h in repo_hrefs if h.count('/') == 2]))[:20]
    print('sample repo candidates', sample_unique[:20])
    # h3 repository title anchors
    h3s = re.findall(r'<h3[^>]*>.*?<a[^>]+href=[\'\"](/[^\'\"]+/[^\'\"]+)[\'\"][^>]*>.*?</a>.*?</h3>', data, re.DOTALL)
    print('h3 anchors count', len(h3s))
    print('h3 sample', h3s[:20])
