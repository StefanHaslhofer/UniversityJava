package jayunit;

import calculator.Calculator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class JayUnit {

    public static void runTests(Class<?> testClass) throws TestClassException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<?> testClassConstructor = testClass.getConstructor();
        Object testClassObject = testClassConstructor.newInstance();

        Method beforeMethod = getBeforeMethod(testClass);
        for (Method m : testClass.getDeclaredMethods()) {
            Optional<Annotation> myTestAnnotation = Arrays.stream(m.getAnnotations())
                    .filter(a -> a.annotationType().equals(MyTest.class)).findFirst();

            if (myTestAnnotation.isPresent()) {
                Method ignoreM = myTestAnnotation.get()
                        .getClass()
                        .getDeclaredMethod("ignore");

                if (ignoreM.invoke(myTestAnnotation.get()).equals(false)) {
                    try {
                        beforeMethod.invoke(testClassObject);
                        m.invoke(testClassObject);
                        printStatus(m.getName(), true);
                    } catch (InvocationTargetException ex) {
                        // don´t throw an error if the exception is actually expected
                        if (m.getAnnotation(ExpectException.class) != null &&
                                ex.getCause().getClass().equals(m.getAnnotation(ExpectException.class).exception())) {
                            printStatus(m.getName(), true);
                        } else {
                            printStatus(m.getName(), false);
                            if (!(ex.getCause() instanceof TestFailedException)) {
                                throw new TestClassException(ex.getMessage(), ex);
                            }
                        }
                    }
                }
            }
        }
    }

    public static void printStatus(String methodName, boolean ok) {
        String status = ok ? " OK." : " failed.";
        System.out.println(methodName + status);
    }

    public static Method getBeforeMethod(Class<?> testClass) throws TestClassException {
        Method beforeMehtod = null;

        // get all declared methods that have a BeforeTest-Annotation
        List<Method> beforeMethods = Arrays.asList(testClass.getDeclaredMethods()).stream()
                .filter(m -> Arrays.asList(m.getAnnotations()).stream()
                        .anyMatch(a -> a.annotationType().equals(BeforeTests.class)))
                .collect(Collectors.toList());

        if (beforeMethods.size() > 1) {
            throw new TestClassException("More than one method annotated with '@BeforeTests'");
        }

        return beforeMethods.isEmpty() ? null : beforeMethods.get(0);
    }
}