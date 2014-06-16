import java.util.Stack;

public class dfsRunnable implements Runnable {

	private Graph graph;
	private boolean[] visited;
	//private Stack<Neighbor> mainStack;

	public dfsRunnable(Graph g, boolean[] vis) {
		graph = g;
		visited = vis;
		//mainStack = st;
	}

	public void run() {
		Stack<Neighbor> workStack = new Stack<>();
		while (graph.covered == false) {
			if (workStack.empty() || workStack == null) {
				workStack = graph.requestStack();
				//System.out.println("We don't have elements in our stack :(");
			} else {
				if (graph.q == false)
					System.out.println("We have " + workStack.size() + " element(s) in our stack!");
				if (!visited[workStack.peek().vertexNum]) {
					if (graph.q == false)
						System.out.println("Thred is doing work!");
					graph.dfs(workStack.pop().vertexNum, visited, workStack);
				}
				else 
					workStack.pop();
				/*Neighbor current = workStack.pop();
				System.out.println("visiting "
						+ graph.adjLists[current.vertexNum].name);
				visited[current.vertexNum] = true;
				for (Neighbor nbr = graph.adjLists[current.vertexNum].adjList; nbr != null; nbr = nbr.next) {
					if (!visited[nbr.vertexNum]) {
						System.out.println("\n"
								+ graph.adjLists[current.vertexNum].name + "--"
								+ graph.adjLists[nbr.vertexNum].name);
						graph.dfs(nbr.vertexNum, visited);
					}
				}*/
			}

		}

	}

}
