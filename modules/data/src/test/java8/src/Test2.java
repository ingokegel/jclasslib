import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER, ElementType.METHOD})
public @interface Test2 {
    String one() default "a";

    boolean two() default false;

    TestEnum three() default TestEnum.TWO;

    Class four() default Integer.class;

    Test other() default @Test;

    Test[] otherArray() default {};
}
