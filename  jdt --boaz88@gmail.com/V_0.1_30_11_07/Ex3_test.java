/** this is a simple name regulator testing file */
public class Ex3_test {
	public static void main(String[] a) {
		// a[0]-map file, a[1]-guards file, a[2]-clients file, a[3]-output file
		if(a==null || a.length<4) {
			a = new String[4];
			a[0] = "terra_13000.tsin";
			a[1] = "G1.txt";
			a[2] = "C1.txt";
			a[3] = "guarding_solution.txt";
		}
		try {
			Ex3.main(a);
		}
		catch(Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
	}
}