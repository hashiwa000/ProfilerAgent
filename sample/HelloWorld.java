import jp.hashiwa.tmp.B;

class HelloWorld {
  public void xxx() {
    System.out.println("xxx");
  }
  public void yyy() {
    System.out.println("yyy");
  }
  public static void main(String[] args) {
    HelloWorld a = new HelloWorld();
    a.xxx();
    a.yyy();
    B b = new B();
    b.xxx();
    b.yyy();
  }
}
