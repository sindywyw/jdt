
package Test;
/**
 * this main class simply test the new Triangulation package, 
 * Updates: 
 * 1. move back to original code (aj see http://www.pi6.fernuni-hagen.de/GeomLab/VoroGlide/index.html.en)
 * 2. add some functionality for the triangulation:
 * 	2.1 find (fast)
 *  2.2 z(x,y): using fast liniar algebra 
 *  2.3 read from file (text - tsin format).
 *  2.4 the triangulation seems to work fast: finding 10000 query points take less then a secound (P4 1.7G)
 *  2.5 very strange bug fix (using sorted insertion).
 * 
 * 3. how to use:
 * 	3.1 construct using Point3D[]. once construct do not change (add points)...
 *  3.2 use the find(x,y), and z(x,y), contains(x,y).
 * 
 * Date 18/8/2004
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.Vector;

import delaunay_triangulation.Delaunay_Triangulation;
import delaunay_triangulation.Point_dt;
import delaunay_triangulation.Triangle_dt;


public class Test {
	public static void main(String[] a) {
		main1();
	//  main2();
	//	main3();
	//	main4();
	//	main5();
	 // main6();
	//	main7();
	}
	public static void main3() {
		MyFrame win = new MyFrame();
		win.start();
	}
	
	   /** this method only tests the write_smf method integrity */
    public static void main1() {
    	try {
    		String file = "data/crater_100000.smf";
    		//String file = "il_1000.smf";
    		//String file = "t1-5000.tsin";
	    	Delaunay_Triangulation dt = new Delaunay_Triangulation(file);
	    	dt.write_smf(file+"_test.smf");
    	}
    	catch(Exception e) {
    		e.printStackTrace();
    	}
    }
	
	    public static void main2 () {
	        int size = 100000, size2 = size;
	        double delta = 1000, delta2 = delta/2;
	        double[] xx=new double[size], yy=new double[size];
	        Point_dt[] ps = new Point_dt[size];
	        double[] xx2=new double[size2], yy2=new double[size2];
	        
	        long start = new Date().getTime();
	        Delaunay_Triangulation ad = new Delaunay_Triangulation();
	        
	        for(int i=0;i<size;i++) {
	        	xx[i] = (Math.random()*delta - (delta*0.1));
	        	yy[i] = (Math.random()*delta - (delta*0.1));
	      
	        	ps[i] = new Point_dt(xx[i], yy[i]);
	        	ad.insertPoint(ps[i]);
	        }
	        long mid = new Date().getTime();
	        
	        for(int i=0;i<size2;i++) {
	        	xx2[i] = (Math.random()*delta2);
	        	yy2[i] = (Math.random()*delta2);
	        }
	        long m1 = new Date().getTime();
	        for(int i=0;i<size2;i++) {
	        	Point_dt p = new Point_dt(xx2[i],yy2[i]);
	        	Triangle_dt t1 = ad.find(p);
	        	if(!t1.contains(p)) {
	        		System.out.println(i+") **ERR: find *** T: "+t1);
	        	} 
	        }
	        long e1 = new Date().getTime();
	        
	        System.out.println("delaunay_triangulation "+ad.size()+" points, "+ad.trianglesSize()+" triangles,  Triangles_td: "+Triangle_dt._counter+"  ,c2: "+Triangle_dt._c2);
	        System.out.println("Constructing time: "+(mid-start));
	        System.out.println("*** E3 find:  time: "+(e1-m1)); 
	        System.out.println("delaunay_triangulation "+ad.size()+" points, "+ad.trianglesSize()+" triangles,  Triangles_td: "+Triangle_dt._counter+"  ,c2: "+Triangle_dt._c2);
		      
	    }
	    /** this method only tests the number of Triangle_dt can be constracted and inserted to a vector,
	     * before a java.lang.OutOfMemoryError is thrown*/
	    public static void main4() {
	    	int i=0;
	    	try {
	    		Point_dt p1 = new Point_dt(1,1);
    			Point_dt p3 = new Point_dt(1,0);
    			Point_dt p2 = new Point_dt(0,0);
	    		Vector vec = new Vector();
	    		while(true) {
	    			double x1 = Math.random(),y1 = Math.random();
	    			double x2 = Math.random(),y2 = Math.random();
	    			//Point_dt p1 = new Point_dt(x1,y1);
	    			//Point_dt p2 = new Point_dt(x2,y2);
	    			//vec.add(p);
	    			Triangle_dt t = new Triangle_dt(p1,p2,p3);
	    			vec.add(t);
	    			i++;
	    			if(i%10000 ==0) System.out.println(i);
	    		}
	    	}
	    	catch(Exception e) {
	    		System.out.println("out of MEMORY: points: "+i);
	    	}
	    }
	    /** this function constructs two triangulations from files (TSIN/SMF) */
	    public static void main5() {
	    	String tsin_file = "../data/tsin/t1-5000.tsin";
	    	try {
	    		Delaunay_Triangulation dt = new Delaunay_Triangulation(tsin_file);
	    		MyFrame win = new MyFrame(dt);
	    		win.start();
	    	}
	    	catch(Exception e) {
	    		System.out.println("ERR: unable to construct a DT from file: "+tsin_file);
	    	} 
	    	String smf_file = "../data/smf/il_5000.smf";
	    	try {
	    		Delaunay_Triangulation dt = new Delaunay_Triangulation(smf_file);
	    		MyFrame win = new MyFrame(dt);
	    		win.start();
	    	}
	    	catch(Exception e) {
	    		System.out.println("ERR: unable to construct a DT from file: "+smf_file);
	    	}
	    	
	    	
	    }
	    /** this function constructs a triangulation from file (TSIN) */
	    public static void main6() {
	    	String tsin_file = "terra_13000.tsin", G_file="G1.tsin", C_file="C1.tsin";
	    	try {
	    		Delaunay_Triangulation dt = new Delaunay_Triangulation(tsin_file);
	    		Visibility vis = new Visibility(dt);
	    		FileReader fr = new FileReader("VisTab.txt");
		    	BufferedReader is = new BufferedReader(fr);
	    		Point_dt[] G = getPoints(G_file);
	    		Point_dt[] C = getPoints(C_file);
	    		 FileWriter fw = new FileWriter("test_LOS_Ex3_new.txt");
	    	     PrintWriter os = new PrintWriter(fw);
	    	  //  int[] test = new int[C.length];
	    	 //   int[] check = {23, 246, 296, 297,547,862,933,1381,1608,1773,2418,2438,2664,2893,3194,3196,3197,3247,3293,3346,3365,3437, 3466,4463,5049,5110,5377,5596,100000};
	    	    		 
	    	    int cc=C.length, c1=0,c2=0, c3=1;
	    		for(int g=0;g<G.length;g++) {
	    			for(int c=0;c<C.length;c++) {
	    				boolean b = vis.los(G[g], C[c]);
		    		//	System.out.println("G:"+g+"  C:"+c+" | Vis: "+G[g]+" "+C[c]+"  =  "+b);
		    			os.println("G:"+g+"  C:"+c+" | Vis: "+G[g]+" "+C[c]+"  =  "+b);
		    	/*		if(b ) {
		    				if(test[c]==0) cc--;
		    				test[c]++; 
		    			}
		    			c1++;
		    			if(c1==check[c2]) {
		    				if(b) {System.out.println("Test line: "+c1);}
		    				c2++;
		    				
		    			}*/
		    			String s = is.readLine();
		    			StringTokenizer st = new StringTokenizer(s,"=",false);
		    			st.nextToken();
		    			String sv = st.nextToken();
		    			boolean val = false;
		    			if(sv.equals("  true")) val=true;
		    			if(b!=val) {
		    				if(b) c1++;
		    				else c2++;
		    				System.out.println("line:"+c3+"  G:"+g+"  C:"+c+"  sv:"+sv+"  val:"+val);
		    			}
		    			c3++;
		    		}
	    			
	    		}
	    		System.out.println("c1:"+c1+"   c2:"+c2);
	    		os.close();
	    	}
	    	catch(Exception e) {
	    		System.out.println("ERR: unable to construct a DT from file: "+tsin_file);
	    		e.printStackTrace();
	    	} 
	    }
	    public static void main7() {
	    	// 155421.0,1063238.0,30.0]  Pt[88125.0,1056111.0,5.0]  =  true
	    	// 146670.0,1010359.0,30.0]  Pt[136461.0,1011278.0,5.0]  =  true
	    	// 95834.28333333333,1020245.3126436782,30.0]  Pt[95834.28333333333,1024613.6528735632,5.0]  =  false
	    	// 174172.31666666665,1049214.3057471265,30.0]  Pt[174172.31666666665,1008059.9425287356,5.0]  =  false
	    	// 
	    	String tsin_file = "terra_13000.tsin", G_file="G1.tsin", C_file="C1.tsin";
	    	try {
	    		Delaunay_Triangulation dt = new Delaunay_Triangulation(tsin_file);
	    		Visibility vis = new Visibility(dt);
	    		Point_dt c = new Point_dt(155421.0,1063238.0,30.0);
	    		Point_dt g = new Point_dt(88125.0,1056111.0,5.0);
	    		Triangle_dt tg = dt.find(g);
	    		Triangle_dt tc = dt.find(c);
	    		System.out.println("T(g):"+tg.z(g));
	    		System.out.println("T(c):"+tc.z(c));
	    	
		    	System.out.println(" | Vis: "+g+" "+c+"  =  "+vis.los(g, c));
		    	Point_dt ppp = new Point_dt(174172.31666666665,1008250.2661290322);
		    	System.out.println(" PPP "+dt.z(ppp));
	    	}
	    	catch(Exception e) {
	    		System.out.println("ERR: unable to construct a DT from file: "+tsin_file);
	    		e.printStackTrace();
	    	} 
	    }
	  static Point_dt[] getPoints(String file) throws Exception {
	    	FileReader fr = new FileReader(file);
	    	BufferedReader is = new BufferedReader(fr);
	    	String s = is.readLine();
	    	
	    	while(s.charAt(0)=='/') s = is.readLine();
	    	StringTokenizer st = new StringTokenizer(s);
	    	int numOfVer = new Integer(s).intValue();
	    	
	    	Point_dt[] ans = new Point_dt[numOfVer];
	    	
	    	// ** reading the file verteces - insert them to the triangulation **
	    	for(int i=0;i<numOfVer;i++) {
	    	    st = new StringTokenizer(is.readLine());
	    	    double d1 = new Double(st.nextToken()).doubleValue();
	    	    double d2 = new Double(st.nextToken()).doubleValue();
	    	    double d3 = new Double(st.nextToken()).doubleValue();
	    	    ans[i] = new Point_dt((int)d1,(int)d2,d3);
	    	}  
	    	return ans;
	     }
}
