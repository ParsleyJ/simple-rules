package parsleyj.simplerules.forward;

import parsleyj.simplerules.KnowledgeBase;
import parsleyj.simplerules.terms.Struct;
import parsleyj.simplerules.terms.Term;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Knowledge base, specialized for forward reasoning algorithms.
 */
public class FCKnowledgeBase extends KnowledgeBase {

    private final DirectoryNode globalFacts = new DirectoryNode(Term.GLOBAL_DIR);


    @Override
    public void addFact(Term fact) {
        super.addFact(fact);
        globalFacts.addTerm(fact);
    }

    @Override
    public void addFacts(List<Term> facts) {
        super.addFacts(facts);
        facts.forEach(globalFacts::addTerm);
    }

    /**
     * Used to minimize the number of candidates, by filtering them to those present in the specified directory
     *
     * @param directory the directory in which the fact must be
     * @return all the facts in the specified directory
     */
    public List<Term> factsInDirectory(List<String> directory) {
        return globalFacts.findTerms(directory);
    }

    @Override
    public FCKnowledgeBase copy() {
        FCKnowledgeBase fckb = new FCKnowledgeBase();
        allFacts.forEach(fckb::addFact);
        fckb.rules.addAll(this.rules);
        return fckb;
    }


    /**
     * Internal class used to organize facts in a tree of "directories" for improved retrieval during the reasoning
     * phase.
     */
    private static class DirectoryNode {
        private final String dirName;
        private final HashMap<String, DirectoryNode> subDirs = new HashMap<>();
        private final List<Term> terms = new ArrayList<>();

        public DirectoryNode(String name) {
            this.dirName = name;
        }

        public String getDirName() {
            return dirName;
        }

        public void addTerm(Term term) {
            addTerm(term, term.directoryPath());
        }

        public List<Term> getExactTerms(List<String> directory) {
            return getNode(directory).map(dn -> dn.terms).orElse(Collections.emptyList());
        }

        public List<Term> findTerms(List<String> directory) {
            return getNode(directory).map(DirectoryNode::getAllTerms).orElse(Collections.emptyList());
        }

        public List<Term> getAllTerms() {
            return this.flatten().stream()
                    .flatMap(dn -> dn.terms.stream())
                    .collect(Collectors.toList());
        }

        private void addTerm(Term term, List<String> directory) {
            getOrGenNode(directory).ifPresent(directoryNode -> directoryNode.terms.add(term));
        }

        private List<DirectoryNode> flatten() {
            List<DirectoryNode> result = new ArrayList<>();
            result.add(this);
            subDirs.forEach((d, dn) -> result.addAll(dn.flatten()));
            return result;
        }

        private Optional<DirectoryNode> getOrGenNode(List<String> directory) {
            if (!directory.isEmpty() && directory.get(0).equals(dirName)) {
                if (directory.size() == 1) {
                    return Optional.of(this);
                } else {
                    List<String> subDir = directory.subList(1, directory.size());
                    if (subDirs.containsKey(subDir.get(0))) {
                        return subDirs.get(subDir.get(0)).getOrGenNode(subDir);
                    } else {
                        DirectoryNode newNode = new DirectoryNode(subDir.get(0));
                        subDirs.put(subDir.get(0), newNode);
                        return newNode.getOrGenNode(subDir);
                    }
                }
            }
            return Optional.empty();
        }

        private Optional<DirectoryNode> getNode(List<String> directory) {
            if (!directory.isEmpty() && directory.get(0).equals(dirName)) {
                if (directory.size() == 1) {
                    return Optional.of(this);
                } else {//(directory.size() > 1)
                    List<String> subDir = directory.subList(1, directory.size());
                    DirectoryNode directoryNode = subDirs.get(subDir.get(0));
                    if (directoryNode != null) {
                        return directoryNode.getNode(subDir);
                    } else {
                        return Optional.empty();
                    }
                }
            }
            return Optional.empty();
        }
    }
}
