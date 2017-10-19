
import org.python.core.Py;
import org.python.core.PyObject; 
import org.python.core.PyInteger;
import org.python.core.PyException;
import org.python.core.PyString;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter; 

public class Embedded { 
  public static void main(String []args) throws PyException {
    
    System.setProperty("python.home", "c:/Program Files/Jython/2.2");
    //System.setProperty("python.path", "c:/Program Files/Jython/2.2");   
    //System.setProperty("python.home", "c:/Program Files/Python24");
    //System.setProperty("python.home", "c:/work/eclipse-workspaces/UNS2/src/main/python/UNS");

      
    PythonInterpreter interp =  new PythonInterpreter(null, new PySystemState());;
    PySystemState sys = Py.getSystemState();
    sys.path.append(new PyString("c:/work/eclipse-workspaces/UNS2/src/main/python"));
    //sys.path.append(new PyString("c:/Program Files/Jython/2.2/Lib"));

    //sys.path.append(new PyString("c:/Program Files/Jython/2.2"));   


    interp.exec("import sys");
    interp.exec("import string");
    interp.exec("import re");
    interp.exec("from NotifTest.ERA.Subscription import Subscription");
    interp.exec("print sys");

    interp.set("a", new PyInteger(42));
    interp.exec("print a");
    interp.exec("x = 2+2");
    PyObject x = interp.get("x");

  }
}




/*
import com.harris.netboss.shared.model.rules.ICommand;
import com.harris.netboss.shared.model.rules.IContext;
import com.harris.netboss.shared.model.rules.PythonFactory;
import com.harris.netboss.shared.model.rules.commands.SetTimer;
import javax.management.Notification;
import org.python.util.PythonInterpreter;
import org.python.core.PyDictionary;
import org.python.core.PyObject;
import java.io.InputStream;
import java.util.Map;
import org.python.core.PyList;
import org.python.core.PyTuple;
import java.util.HashMap;

*//**
 * PythonAdaptor ilements the Adaptor pattern.  
 * It implements an ICommand interface, repackages the arguments 
 * and invokes a jython script.
 *
 * @author Mark Lichtenstein
 * @version %I%, %G%
 * @since NextGen
 * @see com.netboss.harris.shared.model.rules
 *//*
public class PythonAdaptor implements ICommand {

*//** execute: executes the jython interpreter.
 * @param arg[] The input argument to this command. (i.e. Jython Script, URL...)
 * @param arg[0] - an InputStream or String of jython
 * @param arg[1...] - any argument that is a java class.
 * @param event The Indication that triggered this command to be invoked.
 *//*
    public void execute(Notification event, Object[] arg)
    {
        // Get a new or existing PythonInterpreter from PythonFactory
        IContext context = (IContext) arg[0]; // set context from object array
        PythonInterpreter interp = PythonFactory.getInstance(context);
        PyObject localsObject;
        SetTimer setTimer = new SetTimer(); // Create object for Jython
        // Get the local variables in a PyObject if there is a context map
        if (context != null) {
           localsObject = (PyObject) context.get("LOCALS");
           if (localsObject != null)
              interp.setLocals(localsObject);
        }  // end if
    // Pass variables to interpreter's local namespace so that 
    // they may be accessed in a Jython (.py) script
    interp.set("event",(Object) event); 
    interp.set("context",(Object) context); 
        interp.set("setTimer",(Object) setTimer);
    interp.set("args",(Object) arg); 
        if (arg[1] instanceof String)  { // Interpret a string
            interp.exec((String)arg[1]); // run the string
        }  // end if
        if (arg[1] instanceof InputStream) { // execute a file
           InputStream is = (InputStream) arg[1];
           interp.execfile(is);
        }  // end if
        // After executing the jython command(s) we need to restore the
        // context map using any local variables found in the local namespace.
        if (context != null) {
            localsObject = interp.getLocals(); // Get local namespace variables
            context.put("LOCALS",(PyObject)localsObject);
        }
    }  // end of execute method

    *//** toMap: Extract a Python dictionary into a Java Map.
    @param interp The Python interpreter object
    @param pyName The id of the python dictionary
    *//*
    public static Map toMap(PythonInterpreter interp, String pyName){
      PyList pa = ((PyDictionary)interp.get(pyName)).items();
      Map map = new HashMap();
      while(pa.__len__() != 0) {
        PyTuple po = (PyTuple)pa.pop();
        Object first = po.__finditem__(0).__tojava__(Object.class);
        Object second = po.__finditem__(1).__tojava__(Object.class);
        map.put(first, second);
      } // end of while loop
      return map;
   }
}   
*/
