package pt.utl.ist.thesis77077.core;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.nodeTypes.NodeWithIdentifier;

public class TreeWalkerTest {
		
	public void run(String changedFile) {
		
		StaticJavaParser.parse(changedFile).walk(node -> {
            String identifier = "";
            if (node instanceof NodeWithIdentifier)
                identifier = ((NodeWithIdentifier<?>) node).getIdentifier();
            System.out.printf("%-28s %-28s %-48s %s%n",
                              node.getClass().getSimpleName(),
                              identifier,
                              node.getRange().toString(),
                              node.toString().replaceFirst("(?s)\\R.*", "..."));
        });
	}
}
