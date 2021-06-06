# java-evaluation

The test units in this module should be viewed as examples about how stuff
works. Whenever I have issues to get something working properly, I try to
put it in here and try to understand how a simpler variant of the problem
works.

This project depends on https://github.com/jjYBdx4IL/misc/ - master:

    git clone https://github.com/jjYBdx4IL/misc.git
    cd misc
    mvn clean install -DskipTests -DskipITs -Ddependency-check.skip -Dmaven.javadoc.skip

That should give you all dependencies without executing any tests.


## TestAutorunGUI (might work or not, not really using it)

Run

    mvn exec:java -Ptestgui

to start up a GUI that will re-run a selected test unit as soon as
a change in its class is detected. Btw if you are using NetBeans, don't
run this command from within NetBeans as it will disable compile-on-save.



--
devel/java/github/java-evaluation@7867
