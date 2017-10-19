<<<<<<< HEAD:src/test/java/eionet/uns/JythonTest.java
package eionet.uns;

import org.junit.Ignore;
=======
package com.eurodyn.uns;

>>>>>>> dockerize_86798:src/test/java/com/eurodyn/uns/JythonTest.java
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import org.python.core.Py;
import org.python.core.PyObject;
<<<<<<< HEAD:src/test/java/eionet/uns/JythonTest.java
import org.python.core.PyInteger;
import org.python.core.PyException;
import org.python.core.PyString;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;
=======
import org.python.core.PyString;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
>>>>>>> dockerize_86798:src/test/java/com/eurodyn/uns/JythonTest.java

/**
 * Tests for the Python templates.
 */
public class JythonTest {

<<<<<<< HEAD:src/test/java/eionet/uns/JythonTest.java
=======
    private Logger LOGGER = LoggerFactory.getLogger(JythonTest.class);

>>>>>>> dockerize_86798:src/test/java/com/eurodyn/uns/JythonTest.java
    /**
     * Test 2 + 2 is 4
     */
    @Test
    public void execInterpreter() {
        PythonInterpreter interp =  new PythonInterpreter(null, new PySystemState());;
        PySystemState sys = Py.getSystemState();
        interp.exec("x = 2+2");
        PyObject x = interp.get("x");
        assertEquals("4", x.toString());
    }

   
    @Test
    public void importRe() {
        PythonInterpreter interp =  new PythonInterpreter(null, new PySystemState());;
        PySystemState sys = Py.getSystemState();
        interp.exec("import re");
    }

<<<<<<< HEAD:src/test/java/eionet/uns/JythonTest.java
=======
    @Test
    public void testUnicode() {
        PythonInterpreter interpreter = new PythonInterpreter();

        interpreter.set("mystr", new PyString("Søren Roug")); // Test with char from Latin-1
        PyObject myStr = interpreter.get("mystr");
        LOGGER.info(String.valueOf(myStr));

        interpreter.set("mystr", new PyString("€ as Java String"));
        myStr = interpreter.get("mystr");
        LOGGER.info(String.valueOf(myStr));

        interpreter.exec("mystr = u'€ as Python UNICODE'");
        myStr = interpreter.get("mystr");
        LOGGER.info(String.valueOf(myStr));
    }

>>>>>>> dockerize_86798:src/test/java/com/eurodyn/uns/JythonTest.java
}
