package net.sf.jagg;

/**
 * A <code>SelfMethodCall</code> is a <code>MethodCall</code> that simply
 * returns its own object, instead of invoking a <code>Method</code> on that
 * object.
 *
 * @author Randy Gettman
 * @since 0.5.0
 */
public class SelfMethodCall extends MethodCall
{
   private Object myPrototype;

   /**
    * Constructs a <code>SelfMethodCall</code> around an object.
    * @param prototype A prototype object.
    */
   public SelfMethodCall(Object prototype)
   {
      super(null, null);
      myPrototype = prototype;
   }

   /**
    * Returns the return type of the <code>MethodCall</code>, which is in this
    * case the type of the object prototype.
    * @return A <code>Class</code> object representing the return type of the
    *    method, which is in this case the type of the object prototype.
    */
   @Override
   public Class<?> getReturnType()
   {
      return myPrototype.getClass();
   }

   /**
    * Returns the object itself without invoking any <code>Methods</code>.
    * @param object The object on which to "invoke" the <code>Method</code>.
    * @return The object itself.
    */
   @Override
   public Object invoke(Object object)
   {
      return object;
   }
}
