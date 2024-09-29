package algorithm.leetcode.search;

import biz.ingest.JsonUtil;

import java.util.*;

public class WordLadder2 {

    int pathSize = Integer.MAX_VALUE;

    public List<List<String>> findLadders(String beginWord, String endWord, List<String> wordList) {
        List<List<String>> ans = new ArrayList<>();
        List<String> path = new ArrayList<>();
        Set<String> set = new HashSet<>(wordList);
        path.add(beginWord);
        dfs(ans, beginWord, endWord, set, path);
        return ans;
    }

    // 如果事先知道了最短路径长度是 4，那么只需要考虑前 4 层就足够了
    public void dfs(List<List<String>> ans, String word, String target, Set<String> set, List<String> path) {
        if (word.equals(target)) {
            if (path.size() < pathSize) {
                pathSize = path.size();
                ans.clear();
                ans.add(new ArrayList<>(path));
            } else if (path.size() == pathSize) {
                ans.add(new ArrayList<>(path));
            }

            return;
        }

        if (path.size() >= pathSize) {
            return;
        }

        char[] chars = word.toCharArray();

        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];

            for (char k = 'a'; k <= 'z'; k++) {
                if (k == c) {
                    continue;
                }

                chars[i] = k;
                String next = new String(chars);

                if (!path.contains(next) && set.contains(next)) {
                    path.add(next);
                    dfs(ans, next, target, set, path);
                    path.remove(path.size() - 1);
                }
            }

            chars[i] = c;
        }
    }

    public static void main(String[] args) {
        // new WordLadder2().findLadders1("hit", "cog", Arrays.asList("hot","dot","dog","lot","log","cog"));

        List<String> words = Arrays.asList("aaaaa","caaaa","cbaaa","daaaa","dbaaa","eaaaa","ebaaa","faaaa","fbaaa","gaaaa","gbaaa","haaaa","hbaaa","iaaaa","ibaaa","jaaaa","jbaaa","kaaaa","kbaaa","laaaa","lbaaa","maaaa","mbaaa","naaaa","nbaaa","oaaaa","obaaa","paaaa","pbaaa","bbaaa","bbcaa","bbcba","bbdaa","bbdba","bbeaa","bbeba","bbfaa","bbfba","bbgaa","bbgba","bbhaa","bbhba","bbiaa","bbiba","bbjaa","bbjba","bbkaa","bbkba","bblaa","bblba","bbmaa","bbmba","bbnaa","bbnba","bboaa","bboba","bbpaa","bbpba","bbbba","abbba","acbba","dbbba","dcbba","ebbba","ecbba","fbbba","fcbba","gbbba","gcbba","hbbba","hcbba","ibbba","icbba","jbbba","jcbba","kbbba","kcbba","lbbba","lcbba","mbbba","mcbba","nbbba","ncbba","obbba","ocbba","pbbba","pcbba","ccbba","ccaba","ccaca","ccdba","ccdca","cceba","cceca","ccfba","ccfca","ccgba","ccgca","cchba","cchca","cciba","ccica","ccjba","ccjca","cckba","cckca","cclba","cclca","ccmba","ccmca","ccnba","ccnca","ccoba","ccoca","ccpba","ccpca","cccca","accca","adcca","bccca","bdcca","eccca","edcca","fccca","fdcca","gccca","gdcca","hccca","hdcca","iccca","idcca","jccca","jdcca","kccca","kdcca","lccca","ldcca","mccca","mdcca","nccca","ndcca","occca","odcca","pccca","pdcca","ddcca","ddaca","ddada","ddbca","ddbda","ddeca","ddeda","ddfca","ddfda","ddgca","ddgda","ddhca","ddhda","ddica","ddida","ddjca","ddjda","ddkca","ddkda","ddlca","ddlda","ddmca","ddmda","ddnca","ddnda","ddoca","ddoda","ddpca","ddpda","dddda","addda","aedda","bddda","bedda","cddda","cedda","fddda","fedda","gddda","gedda","hddda","hedda","iddda","iedda","jddda","jedda","kddda","kedda","lddda","ledda","mddda","medda","nddda","nedda","oddda","oedda","pddda","pedda","eedda","eeada","eeaea","eebda","eebea","eecda","eecea","eefda","eefea","eegda","eegea","eehda","eehea","eeida","eeiea","eejda","eejea","eekda","eekea","eelda","eelea","eemda","eemea","eenda","eenea","eeoda","eeoea","eepda","eepea","eeeea","ggggg","agggg","ahggg","bgggg","bhggg","cgggg","chggg","dgggg","dhggg","egggg","ehggg","fgggg","fhggg","igggg","ihggg","jgggg","jhggg","kgggg","khggg","lgggg","lhggg","mgggg","mhggg","ngggg","nhggg","ogggg","ohggg","pgggg","phggg","hhggg","hhagg","hhahg","hhbgg","hhbhg","hhcgg","hhchg","hhdgg","hhdhg","hhegg","hhehg","hhfgg","hhfhg","hhigg","hhihg","hhjgg","hhjhg","hhkgg","hhkhg","hhlgg","hhlhg","hhmgg","hhmhg","hhngg","hhnhg","hhogg","hhohg","hhpgg","hhphg","hhhhg","ahhhg","aihhg","bhhhg","bihhg","chhhg","cihhg","dhhhg","dihhg","ehhhg","eihhg","fhhhg","fihhg","ghhhg","gihhg","jhhhg","jihhg","khhhg","kihhg","lhhhg","lihhg","mhhhg","mihhg","nhhhg","nihhg","ohhhg","oihhg","phhhg","pihhg","iihhg","iiahg","iiaig","iibhg","iibig","iichg","iicig","iidhg","iidig","iiehg","iieig","iifhg","iifig","iighg","iigig","iijhg","iijig","iikhg","iikig","iilhg","iilig","iimhg","iimig","iinhg","iinig","iiohg","iioig","iiphg","iipig","iiiig","aiiig","ajiig","biiig","bjiig","ciiig","cjiig","diiig","djiig","eiiig","ejiig","fiiig","fjiig","giiig","gjiig","hiiig","hjiig","kiiig","kjiig","liiig","ljiig","miiig","mjiig","niiig","njiig","oiiig","ojiig","piiig","pjiig","jjiig","jjaig","jjajg","jjbig","jjbjg","jjcig","jjcjg","jjdig","jjdjg","jjeig","jjejg","jjfig","jjfjg","jjgig","jjgjg","jjhig","jjhjg","jjkig","jjkjg","jjlig","jjljg","jjmig","jjmjg","jjnig","jjnjg","jjoig","jjojg","jjpig","jjpjg","jjjjg","ajjjg","akjjg","bjjjg","bkjjg","cjjjg","ckjjg","djjjg","dkjjg","ejjjg","ekjjg","fjjjg","fkjjg","gjjjg","gkjjg","hjjjg","hkjjg","ijjjg","ikjjg","ljjjg","lkjjg","mjjjg","mkjjg","njjjg","nkjjg","ojjjg","okjjg","pjjjg","pkjjg","kkjjg","kkajg","kkakg","kkbjg","kkbkg","kkcjg","kkckg","kkdjg","kkdkg","kkejg","kkekg","kkfjg","kkfkg","kkgjg","kkgkg","kkhjg","kkhkg","kkijg","kkikg","kkljg","kklkg","kkmjg","kkmkg","kknjg","kknkg","kkojg","kkokg","kkpjg","kkpkg","kkkkg","ggggx","gggxx","ggxxx","gxxxx","xxxxx","xxxxy","xxxyy","xxyyy","xyyyy","yyyyy","yyyyw","yyyww","yywww","ywwww","wwwww","wwvww","wvvww","vvvww","vvvwz","avvwz","aavwz","aaawz","aaaaz");
        new WordLadder2().findLadders1("aaaaa", "ggggg", words);
    }

    public List<List<String>> findLadders1(String beginWord, String endWord, List<String> wordList) {
        List<List<String>> ans = new ArrayList<>();
        Map<String, List<String>> adj = new HashMap<>();
        Set<String> words = new HashSet<>(wordList);

        if (!words.contains(endWord)) {
            return ans;
        }

        boolean found = bfs1(beginWord, endWord, words, adj);

        System.out.println(JsonUtil.objToStr(adj));
        System.out.println(adj.size());

        if (!found) {
            return ans;
        }

        Deque<String> path = new ArrayDeque<>();
        path.addFirst(endWord);
        dfs(ans, endWord, beginWord, path, adj);
        return ans;
    }

    public boolean bfs(String beginWord, String endWord, Set<String> words, Map<String, List<String>> adj) {
        Queue<String> queue = new ArrayDeque<>();
        Map<String, Integer> levels = new HashMap<>();
        queue.offer(beginWord);
        levels.put(beginWord, 0);

        int level = 0;
        boolean found = false;

        while (!queue.isEmpty()) {
            int sz = queue.size();
            level++;

            for (int i = 0; i < sz; i++) {
                String word = queue.poll();
                char[] chars = word.toCharArray();

                for (int j = 0; j < chars.length; j++) {
                    char origin = chars[j];

                    for (char c = 'a'; c <= 'z'; c++) {
                        if (c == origin) {
                            continue;
                        }

                        chars[j] = c;
                        String next = new String(chars);

                        // 避免回环

                        // 在考虑第 k 层的某一个单词，如果这个单词在第 1 到 k-1 层已经出现过，就不继续向下探索了（不是最短路径）

                        if (levels.containsKey(next) && levels.get(next) == level) {
                            adj.get(next).add(word);
                            continue;
                        }

                        if (!words.contains(next)) {
                            continue;
                        }

                        // 如果从一个单词扩展出来的单词以前遍历过，距离一定更远，为了避免搜索到已经遍历到，且距离更远的单词，需要将它从 words 中删除
                        words.remove(next);

                        // 这一层扩展出的单词进入队列
                        queue.offer(next);
                        levels.put(next, level);

                        // 记录 next 从 word 而来
                        adj.computeIfAbsent(next, k -> new ArrayList<>()).add(word);

                        if (endWord.equals(next)) {
                            found = true;
                        }
                    }

                    chars[j] = origin;
                }
            }

            if (found) {
                break;
            }
        }

        return found;
    }

    public void dfs(List<List<String>> ans, String word, String beginWord, Deque<String> path, Map<String, List<String>> adj) {
        if (word.equals(beginWord)) {
            ans.add(new ArrayList<>(path));
            return;
        }

        if (!adj.containsKey(word)) {
            return;
        }

        List<String> neighbors = adj.get(word);

        if (neighbors.isEmpty()) {
            return;
        }

        for (String neighbor : neighbors) {
            path.addFirst(neighbor);
            dfs(ans, neighbor, beginWord, path, adj);
            path.removeFirst();
        }
    }

    // 只用 BFS，一边进行层次遍历，一边就保存到目前为止的路径（Queue<List<String>>）。当到达结束单词的时候，得到完整路径。省去进行 DFS 的过程

    // 双向 BFS
    public boolean bfs1(String beginWord, String endWord, Set<String> words, Map<String, List<String>> adj) {
        Set<String> down = new HashSet<>();
        Set<String> up = new HashSet<>();
        down.add(beginWord);
        up.add(endWord);
        boolean direction = true;
        boolean done = false;
        words.remove(endWord);

        while (!down.isEmpty()) {
            Set<String> tmp = down;
            down = up;
            up = tmp;
            direction = !direction;

//            words.removeAll(down);
//            words.removeAll(up);

            Set<String> set = new HashSet<>();

            for (String word : down) {
                char[] chars = word.toCharArray();

                for (int j = 0; j < chars.length; j++) {
                    char origin = chars[j];

                    for (char c = 'a'; c <= 'z'; c++) {
                        if (c == origin) {
                            continue;
                        }

                        chars[j] = c;
                        String next = new String(chars);
                        String key = direction ? next : word;
                        String value = direction ? word : next;

                        if (set.contains(next)) {
                            adj.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
                            continue;
                        }

                        // 两端要相遇的时候，路径上面的所有节点都已经访问过了
                        // 所以要先判断是否相遇
                        if (up.contains(next)) {
                            done = true;
                            adj.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
                            continue;
                        }

                        if (!words.contains(next)) {
                            continue;
                        }

                        words.remove(next);
                        set.add(next);
                        adj.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
                    }

                    chars[j] = origin;
                }
            }

            down = set;

            if (done) {
                break;
            }
        }

        return done;
    }
}
