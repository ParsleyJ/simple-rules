package parsleyj.simplerules.terms;

/**
 * Data type defined by a Java class or interface.
 */
public class JavaType implements Type{

    public static final JavaType Integer = new JavaType(java.lang.Integer.class);

    private final Class<?> clazz;

    /**
     * Creates a Java type defined by the provided class in {@code clazz}
     */
    public JavaType(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    public boolean compatible(Term arg) {
        if(arg.type() instanceof JavaType) {
            return clazz.isAssignableFrom(((JavaType) arg.type()).clazz);
        }
        return false;
    }

    public Class<?> getJavaClass(){
        return clazz;
    }
}
