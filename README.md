[![Open in Gitpod](https://gitpod.io/button/open-in-gitpod.svg)](https://gitpod.io/#https://github.com/alblue/jvmulator) [Open in GitHub Codespaces](https://github.com/codespaces/alblue/alblue-jvmulator-master)

JVMulator
=========

This provides a simple emulator for Java bytecode as well as an in-memory Java
compiler to allow bytecode to be generated. The generated code can be executed
as well as emulated to allow stepping through bytecode line by line, and seeing
what the content of the local variables or stack happens to be.

Usage
-----

The project can be built with Maven or IDEs supporting Maven. There is a Swing
GUI which can be run by executing the Main-Class from the JAR manifest, or
with Maven directly:

$ mvn exec:java
$ java -jar target/jvmulator.jar

The source pane shows the Java source code that will be compiled when the
compile button is pressed. Java source must be compiled before it can be
executed or interpreted. If there are errors when the code is compiled they
will be presented as a dialog. It is intentionally a lightweight text field;
it is not intended that it will be a fully fledged editor.

After the source has been compiled, the drop-down list of methods will be
filled. A method can be chosen and then executed or interpreted by clicking
the appropriate button.

If the interpret button is chosen, a new window will come up with that method's
bytecodes, and the 'step' will allow stepping over an instruction line by line.
When the method returns, the return value will be displayed.

Limitations
-----------

* This isn't intended to be a fully fledged IDE. Think Notepad, not NetBeans.
* The class name Example is hard-coded in some parts; using a different class
  name is not supported at the moment.
* Only static methods can be invoked or emulated.

Limitations for emulation
-------------------------

There are many bytecodes not yet supported, which will cause some failures.

* New isn't supported, which means that many implicit operations like + fail
* Anewarray isn't supported either, so no new Object[] for you
* Anything to do with exception processing (throw, try, catch) isn't present
* Casting (`checkcast`)
* Switch statements (that use `tableswitch` and `lookupswitch`)
* Invokeinterface isn't implemented yet, so `Runnable.run()` won't work
* `invokespecial` used by constructors doesn't work yet
* Synchronisation (`monitorenter` and `monitorexit`) don't work yet
* `wide` operations (used by larger code examples) won't work

If an uninterpreted bytecode occurs, an exception will be generated. So feel
free to try out what you want; just be happy when it works as expected.

Support
-------

There isn't any. Use at your own risk. It is an educational project, originally
created for the presentation for the LJCJUG on 9 June 2020.

That said, if you enjoyed it, feel free to say thanks to @alblue!
