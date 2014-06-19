import java.util.EmptyStackException;
import java.util.Stack;

public class dfsRunnable implements Runnable {

	private Graph graph;
	private boolean[] visited;

	public dfsRunnable(Graph g, boolean[] vis) {
		graph = g;
		visited = vis;
	}

	public void run() {

		Stack<Neighbor> workStack = new Stack<>();

		while (graph.covered == false) {

			if (workStack.empty()) {
				synchronized (graph.mainStack) {
					try {
						graph.mainStack.wait();
						workStack = graph.requestStack();
					} catch (InterruptedException e) {
					}

				}
			}

			try {
				graph.dfs(workStack.pop().vertexNum, visited, workStack);
			} catch (EmptyStackException e) {
				continue;
			}

		}

	}
}
