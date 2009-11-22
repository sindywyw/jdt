package delaunay_triangulation;
/**
 *  this class represents the simplest circle used in Delaunay Triangulation.
 */
class Circle_dt  {

  Point_dt c;
  double r;
  public Circle_dt(){
  }
  public Circle_dt( Point_dt c, double r ) {
    this.c = c;
    this.r = r;
  }
  public Circle_dt( Circle_dt circ) {
    this.c = circ.c;
    this.r = circ.r;
  }
  public String toString() {
    return(new String(" Circle["+ c.toString() + "|" + r + "|" + (int) Math.round(Math.sqrt(r)) + "]"));
  }
}
