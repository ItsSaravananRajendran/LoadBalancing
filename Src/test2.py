while True:
    try:
        result = json.loads('str.json')   # try to parse...
        break                    # parsing worked -> exit loop
    except Exception as e:
        # "Expecting , delimiter: line 34 column 54 (char 1158)"
        # position of unexpected character after '"'
        unexp = int(re.findall(r'\(char (\d+)\)', str(e))[0])
        # position of unescaped '"' before that
        unesc = s.rfind(r'"', 0, unexp)
        s = s[:unesc] + r'\"' + s[unesc+1:]
        # position of correspondig closing '"' (+2 for inserted '\')
        closg = s.find(r'"', unesc + 2)
        s = s[:closg] + r'\"' + s[closg+1:]
print result