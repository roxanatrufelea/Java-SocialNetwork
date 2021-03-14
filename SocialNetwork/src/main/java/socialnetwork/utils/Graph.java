package socialnetwork.utils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;


public class Graph
{

    private int v;
    private int e;
    private Map<Long, HashSet<Long>> adjMap;
    private static Map<Long, Long> visited;
    private static ArrayList<Long> maxComp;
    private static ArrayList<Long> comp;

    public Graph(int vertices)
    {
        v = vertices;
        adjMap = new HashMap<Long, HashSet<Long>>();
        visited = new HashMap<Long, Long>();
        maxComp=new ArrayList<Long>();
        comp=new ArrayList<Long>();
    }

    /***
     * Adds an edge to the graph
     * @param s:Long, source vertex
     * @param d:Long, destination vertex
     */
    public void addEdge(Long s, Long d)
    {
        adjMap.putIfAbsent(s, new HashSet<Long>());
        adjMap.putIfAbsent(d, new HashSet<Long>());
        adjMap.get(s).add(d);
        adjMap.get(s).add(s);
        adjMap.get(d).add(s);
        adjMap.get(d).add(d);
        visited.put(s, 0l);
        visited.put(d, 0l);
    }
    public int nrVisitedVertices(){
        return visited.size();
    }
    /***
     * Returns an array with the longest connected component
     * @return maxComp: ArrayList
     */
    public ArrayList<Long> getMaxComp(){
        nrConnectedComponents();
        return  maxComp;
    }

    /***
     * Marks vertices which can be visited, starting with the given vertex
     * @param vertex
     */
    private void findDFS(Long vertex)
    {

        // Mark as visited
        visited.put(vertex,1l);
        comp.add(vertex);
        for(Long child : adjMap.get(vertex))
        {
            if(visited.get(child) == 0){
                findDFS(child);
            }
        }
    }

    /***
     * Function that returns the number of connected components of the graph
     * @return ccCount: int; the number of connected components
     */
    public int nrConnectedComponents()
    {

        int ccCount=0;
        for(Long vertex : visited.keySet())
        {

            // Check if vertex is already visited or not
            if(visited.get(vertex) == 0)
            {
                comp.clear();
                // Function Call for findDFS
                findDFS(vertex);

                //finds the largest connected component
                if (comp.size()>maxComp.size()) maxComp= (ArrayList<Long>) comp.clone();
                //counts the number of connected components
                ccCount++;

            }
        }
        return ccCount;
    }
}

