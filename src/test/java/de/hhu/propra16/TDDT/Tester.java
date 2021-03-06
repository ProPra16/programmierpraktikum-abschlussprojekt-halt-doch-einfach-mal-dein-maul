package de.hhu.propra16.TDDT;

import org.junit.Test;
import vk.core.api.JavaStringCompiler;
import static org.junit.Assert.assertEquals;


public class Tester {

    private JavaStringCompiler compiler;
    private String ActualGreenCode;
    private char Phase='R';

    @Test
    public void testREDtoGREENShouldfail1() { //Kein fehlschlagender Test implementiert
        UserCode userCode=new UserCode("Foo");
        userCode.setTest("@Test public void test() {assertEquals(1,1);}");
        ActionUnit newCompile=new ActionUnit(userCode);
        newCompile.checkGREEN();
        compiler=newCompile.getCompiler();
        GreenValidator NextPhase=new GreenValidator(compiler);
        if (NextPhase.isValid()) {
            Phase='G';
        }
        assertEquals('R',Phase);
    }

    @Test
    public void testREDtoGREENShouldFail2() { //Keine Tests implementiert
        UserCode userCode=new UserCode("Foo");
        userCode.setTest("private String Foo;");
        ActionUnit newCompile=new ActionUnit(userCode);
        newCompile.checkGREEN();
        compiler=newCompile.getCompiler();
        GreenValidator NextPhase=new GreenValidator(compiler);
        if (NextPhase.isValid()) {
            Phase='G';
        }
        assertEquals('R',Phase);
    }

    @Test
    public void testREDtoGREENShouldFail3() { //Mehrere Test schlagen fehl
        UserCode userCode=new UserCode("Foo");
        userCode.setTest("@Test " + "public void test1() {" + "assertEquals(1,2);" + "}"+
                "@Test " + "public void test2() {" + "assertEquals(1,2);" +"}");
        ActionUnit newCompile=new ActionUnit(userCode);
        newCompile.checkGREEN();
        compiler=newCompile.getCompiler();
        GreenValidator NextPhase=new GreenValidator(compiler);
        if (NextPhase.isValid()) {
            Phase='G';
        }
        assertEquals('R',Phase);
    }

    @Test
    public void testREDtoGREENShouldFail4() { //Genau ein Test schlägt fehl und kompiliert nicht
        UserCode userCode=new UserCode("Foo");
        userCode.setTest("@Test public void test() {assertEquals(4,Foo.f(1));}");
        ActionUnit newCompile=new ActionUnit(userCode);
        newCompile.checkGREEN();
        compiler=newCompile.getCompiler();
        GreenValidator NextPhase=new GreenValidator(compiler);
        if (NextPhase.isValid()) {
            Phase='G';
        }
        assertEquals('G',Phase);
    }

    @Test
    public void testREDtoGREENShouldPass1() { //Test kompiliert nicht
        UserCode userCode=new UserCode("Foo");
        userCode.setTest("I am bad");
        ActionUnit newCompile=new ActionUnit(userCode);
        newCompile.checkGREEN();
        compiler=newCompile.getCompiler();
        GreenValidator NextPhase=new GreenValidator(compiler);
        if (NextPhase.isValid()) {
            Phase='G';
        }
        assertEquals('G',Phase);
    }

    @Test
    public void testREDtoGREENShouldPass2() { //Genau ein Test schlägt fehl
        UserCode userCode=new UserCode("Foo");
        userCode.setTest("@Test public void test() {assertEquals(2,1);}");
        ActionUnit newCompile=new ActionUnit(userCode);
        newCompile.checkGREEN();
        compiler=newCompile.getCompiler();
        GreenValidator NextPhase=new GreenValidator(compiler);
        if (NextPhase.isValid()) {
            Phase='G';
        }
        assertEquals('G',Phase);
    }

    @Test
    public void testGREENtoRED() { //Überprüfe, ob Tests noch da sind
        UserCode userCode=new UserCode("Foo");
        userCode.setTest("@Test public void test() {assertEquals(4,Foo.f(1));}");
        Phase='G';
        ActualGreenCode=userCode.getClassCode();
        userCode.setClass(ActualGreenCode);
        Phase='R';
        userCode.setTest(userCode.getTestCode());
        assertEquals("@Test public void test() {assertEquals(4,Foo.f(1));}",userCode.getTestCode());
    }

    @Test
    public void testGREENtoREDDeleting() { //Überprüfe, ob beim Wechsel von GreenValidator nach RED der Code gelöscht wird
        UserCode userCode=new UserCode("Foo");
        Phase='G';
        userCode.setClass("public static int f(int N) {return 4;}");
        userCode.setTest("@Test public void test() {assertEquals(4,Foo.f(1));}");
        ActualGreenCode=userCode.getClassCode();
        userCode.setClass("Ich veränder mal die Klasse, macht ja Nix aus !");
        Phase='R';
        userCode.setTest(userCode.getTestCode());
        assertEquals("public static int f(int N) {return 4;}",ActualGreenCode);
    }

    @Test
    public void GREENtoREFACTORFails1() { //Implementierte Klasse kompiliert nicht
        Phase='G';
        UserCode userCode=new UserCode("Foo");
        userCode.setClass("I am Bad");
        userCode.setTest("@Test public void test() {assertEquals(4,Foo.f(1));}");
        ActionUnit newCompile=new ActionUnit(userCode);
        newCompile.compile();
        if (!newCompile.compileErrors()) {
            Phase='F';
        }
        else {
            Phase='G';
        }
        assertEquals('G',Phase);
    }

    @Test
    public void GREENtoREFACTORFails2() { //Implementierte Klasse kompiliert, Tests werden aber nicht bestanden
        UserCode userCode=new UserCode("Foo");
        userCode.setClass("public static int f(int N) {return 1;}");
        userCode.setTest("@Test public void test() {assertEquals(100,Foo.f(1));}");
        ActionUnit newCompile=new ActionUnit(userCode);
        newCompile.compileAndTest();
        assertEquals(false,newCompile.hasnoFailedTests());
    }

    @Test
    public void GREENtoREFACTORFails3() { //Implementierte Klasse kompiliert, Tests sind aber fehlerhaft
        UserCode userCode=new UserCode("Foo");
        userCode.setClass("public static int f(int N) {return 1;}");
        userCode.setTest("I am Bad");
        ActionUnit newCompile=new ActionUnit(userCode);
        newCompile.compileAndTest();
        assertEquals(true,newCompile.compileErrors());
    }

    @Test
    public void GREENtoREFACTORFPasses1() { //Implementierte Klasse kompiliert, Tests sind ok
        UserCode userCode=new UserCode("Foo");
        userCode.setClass("public static int f(int N) {return 1;}");
        userCode.setTest("@Test public void test() {assertEquals(1,Foo.f(1));}");
        ActionUnit newCompile=new ActionUnit(userCode);
        newCompile.compileAndTest();
        assertEquals(true,newCompile.hasnoFailedTests());
    }

    @Test
    public void GREENtoREFACTORFPasses2() { //Implementierte Klasse kompiliert, Tests sind ok
        UserCode userCode=new UserCode("Foo");
        userCode.setClass("public static int f(int N) {return N;}");
        userCode.setTest("@Test public void test1() {assertEquals(1,Foo.f(1));}"+
                "@Test public void test2() {assertEquals(2,Foo.f(2));}"+
                "@Test public void test3() {assertEquals(3,Foo.f(3));}"
        );
        ActionUnit newCompile=new ActionUnit(userCode);
        newCompile.compileAndTest();
        assertEquals(true,newCompile.hasnoFailedTests());
    }

}