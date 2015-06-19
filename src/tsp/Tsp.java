package tsp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Tsp {

/*	public static void main(String[] args) {
        // TODO Auto-generated method stub
		Tsp t = new Tsp();
		
		int i = 0, j = 0;
		
		double c[][]=
		{
		  {0,3,6,7},
		  {5,0,2,3},
		  {6,4,0,2},
		  {3,7,5,0},
		};
		
		int line = c.length;
		int column = (int)Math.pow(2, line-1); 
		
		int V[][] = t.computeV(line);
				
		double d[][]= new double[line][column];
		
		for(i=0; i < line; i++) 
	        for(j=0; j < column; j++) 
	            d[i][j] = Double.MAX_VALUE; 
		List<Integer> path = new ArrayList<Integer>();
	    t.TSP(d,c,V,line,path); 
	    
	    for(i = 0; i < path.size(); i++)
	    {
	    	System.out.println(path.get(i));
	    }
	    System.out.print("The least length of road is: ");
	    System.out.print(d[0][column-1]);
		
	/*	Point p1 = new Point(0,0);
		Point p2 = new Point(1,0);
		Point p3 = new Point(0,1);
		Point p4 = new Point(1,1);
		
		List<Point> p = new ArrayList<Point>();
		
		p.add(p1);
		p.add(p2);
		p.add(p3);
		p.add(p4);
		
		t.computeTsp(p);
				
	}*/

    public List<Integer> computeTsp(List<Point> p) {
        double c[][] = computeDis(p);
        int V[][] = computeV(c.length);
        List<Integer> path = new ArrayList<Integer>();

        int line = c.length;
        int column = (int) Math.pow(2, line - 1);

        Tsp t = new Tsp();

        double d[][] = new double[line][column];

        for (int i = 0; i < line; i++)
            for (int j = 0; j < column; j++)
                d[i][j] = Double.MAX_VALUE;

        t.TSP(d, c, V, line, path);

        if (d[0][column - 1] == Double.MAX_VALUE) {
            d[0][column - 1] = 0;
        }
        System.out.print("The least length of road is: ");
        System.out.print(d[0][column - 1]);
        System.out.println();

        return path;
    }

    public double[][] computeDis(List<Point> p) {
        double dis[][] = new double[p.size()][p.size()];
        int i = 0, j = 0;

        for (i = 0; i < p.size(); i++) {
            for (j = 0; j < p.size(); j++) {
                dis[i][j] = Math.sqrt((p.get(j).getX() - p.get(i).getX()) * (p.get(j).getX() - p.get(i).getX())
                        + (p.get(j).getY() - p.get(i).getY()) * (p.get(j).getY() - p.get(i).getY()));
            }
        }

        return dis;
    }

    public int[][] computeV(int numOfPoint) {
        int line = (int) Math.pow(2.0, numOfPoint - 1);
        int column = numOfPoint - 1;
        int V[][] = new int[line][column];
        int num[] = new int[column];
        int V_wid[] = new int[column];
        int V_wid_tmp[] = new int[column];
        int count = 0, temp = 0;

        int i = 0, j = 0;


        for (i = 0; i < numOfPoint - 1; i++) {
            num[i] = i + 1;
            V_wid[i] = 0;
        }

        while (count < line) {
            temp = count;
            System.arraycopy(V_wid, 0, V_wid_tmp, 0, column);
            j = 0;
            while (temp != 0) {
                V_wid_tmp[j] = temp % 2;
                temp = temp / 2;
                j++;
            }

            for (int k = 0; k < column; k++) {
                if (V_wid_tmp[k] == 1) {
                    V_wid_tmp[k] = num[k];
                }
            }

            System.arraycopy(V_wid_tmp, 0, V[count], 0, column);
            count++;
        }


        return V;
    }

    public boolean isInclude(int x, int array[]) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == x)
                return true;
        }
        return false;
    }

    public int Left(int k, int array[], int V[][]) //实现V'-{k}的下标检索
    {
        int i = 0, index = 0;
        int j = 0;
        int array_count[] = new int[array.length + 1];
        int V_count[] = new int[array.length + 1];
        int temp[] = new int[array.length];

        for (i = 0; i < array.length; i++) {
            temp[i] = array[i];
        }

        for (i = 0; i < array.length; i++) {
            if (temp[i] == k) {
                temp[i] = 0; //相当于去掉k这个城市
            }
        }

        for (i = 0; i < array.length; i++) {
            for (j = 0; j <= array.length; j++) {
                if (temp[i] == j) {
                    array_count[j]++;
                }
            }
        }

        for (index = 0; index < V.length; index++) {
            for (i = 0; i < array.length; i++) {
                for (j = 0; j <= array.length; j++) {
                    if (V[index][i] == j) {
                        V_count[j]++;
                    }
                }

            }

            boolean flag = true;

            for (i = 0; i <= array.length; i++) {
                if (array_count[i] != V_count[i]) {
                    flag = false;
                    break;
                }
            }


            if (flag) {
                return index;
            }

            for (i = 0; i <= array.length; i++) {
                V_count[i] = 0;
            }

        }

        return 0;
    }

    public void TSP(double d[][], double c[][], int V[][], int n, List<Integer> lpath) {
        int i = 0, j = 0, k = 0, m = 1;
        int path[] = new int[n + 1];
        int index = (int) Math.pow(2, n - 1) - 1;

        for (i = 1; i < n; i++) //V'为空时赋值
        {
            d[i][0] = c[i][0];
        }

        for (j = 1; j < index; j++) {
            for (i = 1; i < n; i++) {
                if (!isInclude(i, V[j])) {
                    for (k = 0; k < n - 1; k++) {
                        if ((V[j][k] != 0) && ((c[i][V[j][k]] + d[V[j][k]][Left(V[j][k], V[j], V)]) < d[i][j])) {
                            d[i][j] = c[i][V[j][k]] + d[V[j][k]][Left(V[j][k], V[j], V)];
                        }
                    }
                }
            }
        }

        for (k = 0; k < n - 1; k++) {
            if ((V[index][k] != 0) && (c[0][V[index][k]] + d[V[index][k]][Left(V[index][k], V[index], V)]) < d[0][index]) {
                d[0][index] = c[0][V[index][k]] + d[V[index][k]][Left(V[index][k], V[index], V)];
                path[m] = V[index][k];
            }
        }

        //输出路径

        int num[] = new int[n - 1];
        int num_tmp[] = new int[n - 1];
        int compare[] = new int[n - 1];
        int pre_location_j = 0, location_i = 0, location_j = 0;

        for (i = 0; i < n - 1; i++) {
            num[i] = i + 1;
            compare[i] = 0;
        }

        if (path[m] - 1 >= 0) {
            num[path[m] - 1] = 0;

            while (!Arrays.equals(num, compare)) {
                for (j = 0; j < V.length; j++) {
                    if (Arrays.equals(num, V[j])) {
                        pre_location_j = j;
                        break;
                    }
                }

                for (i = 0; i < n - 1; i++) {
                    System.arraycopy(num, 0, num_tmp, 0, n - 1);
                    if (num_tmp[i] != 0) {
                        location_i = num_tmp[i];
                        num_tmp[i] = 0;
                        for (j = 0; j < V.length; j++) {
                            if (Arrays.equals(num_tmp, V[j])) {
                                location_j = j;
                                break;
                            }
                        }

                        if (c[path[m]][location_i] + d[location_i][location_j] == d[path[m]][pre_location_j]) {
                            m++;
                            path[m] = location_i;
                        }
                    }
                }
                num[path[m] - 1] = 0;
            }
        }

        for (i = 0; i < path.length; i++) {
            lpath.add(path[i]);
        }
    }
}
