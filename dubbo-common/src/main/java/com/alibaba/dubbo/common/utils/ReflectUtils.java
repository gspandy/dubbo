/*
 * Copyright 1999-2011 Alibaba Group.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.dubbo.common.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.NotFoundException;

/**
 * ReflectUtils
 * 
 * @author qian.lei
 */

public final class ReflectUtils
{
	/**
	 * void(V).
	 */
	public static final char JVM_VOID = 'V';

	/**
	 * boolean(Z).
	 */
	public static final char JVM_BOOLEAN = 'Z';

	/**
	 * byte(B).
	 */
	public static final char JVM_BYTE = 'B';

	/**
	 * char(C).
	 */
	public static final char JVM_CHAR = 'C';

	/**
	 * double(D).
	 */
	public static final char JVM_DOUBLE = 'D';

	/**
	 * float(F).
	 */
	public static final char JVM_FLOAT = 'F';

	/**
	 * int(I).
	 */
	public static final char JVM_INT = 'I';

	/**
	 * long(J).
	 */
	public static final char JVM_LONG = 'J';

	/**
	 * short(S).
	 */
	public static final char JVM_SHORT = 'S';

	public static final Class<?>[] EMPTY_CLASS_ARRAY = new Class<?>[0];

	public static final String JAVA_IDENT_REGEX = "(?:[_$a-zA-Z][_$a-zA-Z0-9]*)";

	public static final String JAVA_NAME_REGEX = "(?:" + JAVA_IDENT_REGEX + "(?:\\." + JAVA_IDENT_REGEX + ")*)";

	public static final String CLASS_DESC = "(?:L" + JAVA_IDENT_REGEX   + "(?:\\/" + JAVA_IDENT_REGEX + ")*;)";

	public static final String ARRAY_DESC  = "(?:\\[+(?:(?:[VZBCDFIJS])|" + CLASS_DESC + "))";

	public static final String DESC_REGEX = "(?:(?:[VZBCDFIJS])|" + CLASS_DESC + "|" + ARRAY_DESC + ")";

	public static final Pattern DESC_PATTERN = Pattern.compile(DESC_REGEX);

	public static final String METHOD_DESC_REGEX = "(?:("+JAVA_IDENT_REGEX+")?\\(("+DESC_REGEX+"*)\\)("+DESC_REGEX+")?)";

	public static final Pattern METHOD_DESC_PATTERN = Pattern.compile(METHOD_DESC_REGEX);

	public static final Pattern GETTER_METHOD_DESC_PATTERN = Pattern.compile("get([A-Z][_a-zA-Z0-9]*)\\(\\)(" + DESC_REGEX + ")");

	public static final Pattern SETTER_METHOD_DESC_PATTERN = Pattern.compile("set([A-Z][_a-zA-Z0-9]*)\\((" + DESC_REGEX + ")\\)V");

	public static final Pattern IS_HAS_CAN_METHOD_DESC_PATTERN = Pattern.compile("(?:is|has|can)([A-Z][_a-zA-Z0-9]*)\\(\\)Z");

	/**
	 * is compatible.
	 * 
	 * @param c class.
	 * @param o instance.
	 * @return compatible or not.
	 */
	public static boolean isCompatible(Class<?> c, Object o)
	{
		boolean pt = c.isPrimitive();
		if( o == null )
			return !pt;

		if( pt )
		{
			if( c == int.class )
				c = Integer.class;
			else if( c == boolean.class )
				c = Boolean.class;
			else  if( c == long.class )
				c = Long.class;
			else if( c == float.class )
				c = Float.class;
			else if( c == double.class )
				c = Double.class;
			else if( c == char.class )
				c = Character.class;
			else if( c == byte.class )
				c = Byte.class;
			else if( c == short.class )
				c = Short.class;
		}
		if( c == o.getClass() )
			return true;
		return c.isInstance(o);
	}

	/**
	 * is compatible.
	 * 
	 * @param cs class array.
	 * @param os object array.
	 * @return compatible or not.
	 */
	public static boolean isCompatible(Class<?>[] cs, Object[] os)
	{
		int len = cs.length;
		if( len != os.length ) return false;
		if( len == 0 ) return true;
		for(int i=0;i<len;i++)
			if( !isCompatible(cs[i], os[i]) ) return false;
		return true;
	}
	
	public static String getCodeBase(Class<?> cls) {
	    if (cls == null)
	        return null;
	    ProtectionDomain domain = cls.getProtectionDomain();
	    if (domain == null)
	        return null;
	    CodeSource source = domain.getCodeSource();
	    if (source == null)
	        return null;
	    URL location = source.getLocation();
	    if (location == null)
            return null;
	    return location.getFile();
	}

	/**
	 * get name.
	 * java.lang.Object[][].class => "java.lang.Object[][]"
	 * 
	 * @param c class.
	 * @return name.
	 */
	public static String getName(Class<?> c)
	{
		if( c.isArray() )
		{
			StringBuilder sb = new StringBuilder();
			do
			{
				sb.append("[]");
				c = c.getComponentType();
			}
			while( c.isArray() );

			return c.getName() + sb.toString();
		}
		return c.getName();
	}

	/**
	 * get method name.
	 * "void do(int)", "void do()", "int do(java.lang.String,boolean)"
	 * 
	 * @param m method.
	 * @return name.
	 */
	public static String getName(final Method m)
	{
		StringBuilder ret = new StringBuilder();
		ret.append(getName(m.getReturnType())).append(' ');
		ret.append(m.getName()).append('(');
		Class<?>[] parameterTypes = m.getParameterTypes();
		for(int i=0;i<parameterTypes.length;i++)
		{
			if( i > 0 )
				ret.append(',');
			ret.append(getName(parameterTypes[i]));
		}
		ret.append(')');
		return ret.toString();
	}
	
	public static String getSignature(String methodName, Class<?>[] parameterTypes) {
		StringBuilder sb = new StringBuilder(methodName);
		sb.append("(");
		if (parameterTypes != null && parameterTypes.length > 0) {
			boolean first = true;
			for (Class<?> type : parameterTypes) {
				if (first) {
					first = false;
				} else {
					sb.append(",");
				}
				sb.append(type.getName());
			}
		}
		sb.append(")");
		return sb.toString();
	}

	/**
	 * get constructor name.
	 * "()", "(java.lang.String,int)"
	 * 
	 * @param c constructor.
	 * @return name.
	 */
	public static String getName(final Constructor<?> c)
	{
		StringBuilder ret = new StringBuilder("(");
		Class<?>[] parameterTypes = c.getParameterTypes();
		for(int i=0;i<parameterTypes.length;i++)
		{
			if( i > 0 )
				ret.append(',');
			ret.append(getName(parameterTypes[i]));
		}
		ret.append(')');
		return ret.toString();
	}

	/**
	 * get class desc.
	 * boolean[].class => "[Z"
	 * Object.class => "Ljava/lang/Object;"
	 * 
	 * @param c class.
	 * @return desc.
	 * @throws NotFoundException 
	 */
	public static String getDesc(Class<?> c)
	{
		StringBuilder ret = new StringBuilder();

		while( c.isArray() )
		{
			ret.append('[');
			c = c.getComponentType();
		}

		if( c.isPrimitive() )
		{
			String t = c.getName();
			if( "void".equals(t) ) ret.append(JVM_VOID);
			else if( "boolean".equals(t) ) ret.append(JVM_BOOLEAN);
			else if( "byte".equals(t) ) ret.append(JVM_BYTE);
			else if( "char".equals(t) ) ret.append(JVM_CHAR);
			else if( "double".equals(t) ) ret.append(JVM_DOUBLE);
			else if( "float".equals(t) ) ret.append(JVM_FLOAT);
			else if( "int".equals(t) ) ret.append(JVM_INT);
			else if( "long".equals(t) ) ret.append(JVM_LONG);
			else if( "short".equals(t) ) ret.append(JVM_SHORT);
		}
		else
		{
			ret.append('L');
			ret.append(c.getName().replace('.', '/'));
			ret.append(';');
		}
		return ret.toString();
	}

	/**
	 * get class array desc.
	 * [int.class, boolean[].class, Object.class] => "I[ZLjava/lang/Object;"
	 * 
	 * @param cs class array.
	 * @return desc.
	 * @throws NotFoundException 
	 */
	public static String getDesc(final Class<?>[] cs)
	{
		if( cs.length == 0 )
			return "";

		StringBuilder sb = new StringBuilder(64);
		for( Class<?> c : cs )
			sb.append(getDesc(c));
		return sb.toString();
	}

	/**
	 * get method desc.
	 * int do(int arg1) => "do(I)I"
	 * void do(String arg1,boolean arg2) => "do(Ljava/lang/String;Z)V"
	 * 
	 * @param m method.
	 * @return desc.
	 */
	public static String getDesc(final Method m)
	{
		StringBuilder ret = new StringBuilder(m.getName()).append('(');
		Class<?>[] parameterTypes = m.getParameterTypes();
		for(int i=0;i<parameterTypes.length;i++)
			ret.append(getDesc(parameterTypes[i]));
		ret.append(')').append(getDesc(m.getReturnType()));
		return ret.toString();
	}

	/**
	 * get constructor desc.
	 * "()V", "(Ljava/lang/String;I)V"
	 * 
	 * @param c constructor.
	 * @return desc
	 */
	public static String getDesc(final Constructor<?> c)
	{
		StringBuilder ret = new StringBuilder("(");
		Class<?>[] parameterTypes = c.getParameterTypes();
		for(int i=0;i<parameterTypes.length;i++)
			ret.append(getDesc(parameterTypes[i]));
		ret.append(')').append('V');
		return ret.toString();
	}

	/**
	 * get method desc.
	 * "(I)I", "()V", "(Ljava/lang/String;Z)V"
	 * 
	 * @param m method.
	 * @return desc.
	 */
	public static String getDescWithoutMethodName(Method m)
	{
		StringBuilder ret = new StringBuilder();
		ret.append('(');
		Class<?>[] parameterTypes = m.getParameterTypes();
		for(int i=0;i<parameterTypes.length;i++)
			ret.append(getDesc(parameterTypes[i]));
		ret.append(')').append(getDesc(m.getReturnType()));
		return ret.toString();
	}

	/**
	 * get class desc.
	 * Object.class => "Ljava/lang/Object;"
	 * boolean[].class => "[Z"
	 * 
	 * @param c class.
	 * @return desc.
	 * @throws NotFoundException 
	 */
	public static String getDesc(final CtClass c) throws NotFoundException
	{
		StringBuilder ret = new StringBuilder();
		if( c.isArray() )
		{
			ret.append('[');
			ret.append(getDesc(c.getComponentType()));
		}
		else if( c.isPrimitive() )
		{
			String t = c.getName();
			if( "void".equals(t) ) ret.append(JVM_VOID);
			else if( "boolean".equals(t) ) ret.append(JVM_BOOLEAN);
			else if( "byte".equals(t) ) ret.append(JVM_BYTE);
			else if( "char".equals(t) ) ret.append(JVM_CHAR);
			else if( "double".equals(t) ) ret.append(JVM_DOUBLE);
			else if( "float".equals(t) ) ret.append(JVM_FLOAT);
			else if( "int".equals(t) ) ret.append(JVM_INT);
			else if( "long".equals(t) ) ret.append(JVM_LONG);
			else if( "short".equals(t) ) ret.append(JVM_SHORT);
		}
		else
		{
			ret.append('L');
			ret.append(c.getName().replace('.','/'));
			ret.append(';');
		}
		return ret.toString();
	}

	/**
	 * get method desc.
	 * "do(I)I", "do()V", "do(Ljava/lang/String;Z)V"
	 * 
	 * @param m method.
	 * @return desc.
	 */
	public static String getDesc(final CtMethod m) throws NotFoundException
	{
		StringBuilder ret = new StringBuilder(m.getName()).append('(');
		CtClass[] parameterTypes = m.getParameterTypes();
		for(int i=0;i<parameterTypes.length;i++)
			ret.append(getDesc(parameterTypes[i]));
		ret.append(')').append(getDesc(m.getReturnType()));
		return ret.toString();
	}

	/**
	 * get constructor desc.
	 * "()V", "(Ljava/lang/String;I)V"
	 * 
	 * @param c constructor.
	 * @return desc
	 */
	public static String getDesc(final CtConstructor c) throws NotFoundException
	{
		StringBuilder ret = new StringBuilder("(");
		CtClass[] parameterTypes = c.getParameterTypes();
		for(int i=0;i<parameterTypes.length;i++)
			ret.append(getDesc(parameterTypes[i]));
		ret.append(')').append('V');
		return ret.toString();
	}

	/**
	 * get method desc.
	 * "(I)I", "()V", "(Ljava/lang/String;Z)V".
	 * 
	 * @param m method.
	 * @return desc.
	 */
	public static String getDescWithoutMethodName(final CtMethod m) throws NotFoundException
	{
		StringBuilder ret = new StringBuilder();
		ret.append('(');
		CtClass[] parameterTypes = m.getParameterTypes();
		for(int i=0;i<parameterTypes.length;i++)
			ret.append(getDesc(parameterTypes[i]));
		ret.append(')').append(getDesc(m.getReturnType()));
		return ret.toString();
	}

	/**
	 * name to desc.
	 * java.util.Map[][] => "[[Ljava/util/Map;"
	 * 
	 * @param name name.
	 * @return desc.
	 */
	public static String name2desc(String name)
	{
		StringBuilder sb = new StringBuilder();
		int c = 0,index = name.indexOf('[');
		if( index > 0 )
		{
			c = ( name.length() - index ) / 2;
			name = name.substring(0,index);
		}
		while( c-- > 0 ) sb.append("[");
		if( "void".equals(name) ) sb.append(JVM_VOID);
		else if( "boolean".equals(name) ) sb.append(JVM_BOOLEAN);
		else if( "byte".equals(name) ) sb.append(JVM_BYTE);
		else if( "char".equals(name) ) sb.append(JVM_CHAR);
		else if( "double".equals(name) ) sb.append(JVM_DOUBLE);
		else if( "float".equals(name) ) sb.append(JVM_FLOAT);
		else if( "int".equals(name) ) sb.append(JVM_INT);
		else if( "long".equals(name) ) sb.append(JVM_LONG);
		else if( "short".equals(name) ) sb.append(JVM_SHORT);
		else sb.append('L').append(name.replace('.', '/')).append(';');
		return sb.toString();
	}

	/**
	 * desc to name.
	 * "[[I" => "int[][]"
	 * 
	 * @param desc desc.
	 * @return name.
	 */
	public static String desc2name(String desc)
	{
		StringBuilder sb = new StringBuilder();
		int c = desc.lastIndexOf('[') + 1;
		if( desc.length() == c+1 )
		{
			switch( desc.charAt(c) )
			{
				case JVM_VOID: { sb.append("void"); break; }
				case JVM_BOOLEAN: { sb.append("boolean"); break; }
				case JVM_BYTE: { sb.append("byte"); break; }
				case JVM_CHAR: { sb.append("char"); break; }
				case JVM_DOUBLE: { sb.append("double"); break; }
				case JVM_FLOAT: { sb.append("float"); break; }
				case JVM_INT: { sb.append("int"); break; }
				case JVM_LONG: { sb.append("long"); break; }
				case JVM_SHORT: { sb.append("short"); break; }
				default:
					throw new RuntimeException();
			}
		}
		else
		{
			sb.append(desc.substring(c+1, desc.length()-1).replace('/','.'));
		}
		while( c-- > 0 ) sb.append("[]");
		return sb.toString();
	}
	
	public static Class<?> forName(String name) {
		try {
			return name2class(name);
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException("Not found class " + name + ", cause: " + e.getMessage(), e);
		}
	}

	/**
	 * name to class.
	 * "boolean" => boolean.class
	 * "java.util.Map[][]" => java.util.Map[][].class
	 * 
	 * @param name name.
	 * @return Class instance.
	 */
	public static Class<?> name2class(String name) throws ClassNotFoundException
	{
		return name2class(ClassHelper.getClassLoader(), name);
	}

	/**
	 * name to class.
	 * "boolean" => boolean.class
	 * "java.util.Map[][]" => java.util.Map[][].class
	 * 
	 * @param cl ClassLoader instance.
	 * @param name name.
	 * @return Class instance.
	 */
	public static Class<?> name2class(ClassLoader cl, String name) throws ClassNotFoundException
	{
		int c = 0, index = name.indexOf('[');
		if( index > 0 )
		{
			c = ( name.length() - index ) / 2;
			name = name.substring(0, index);
		}
		if( c > 0 )
		{
			StringBuilder sb = new StringBuilder();
			while( c-- > 0 )
				sb.append("[");

			if( "void".equals(name) ) sb.append(JVM_VOID);
			else if( "boolean".equals(name) ) sb.append(JVM_BOOLEAN);
			else if( "byte".equals(name) ) sb.append(JVM_BYTE);
			else if( "char".equals(name) ) sb.append(JVM_CHAR);
			else if( "double".equals(name) ) sb.append(JVM_DOUBLE);
			else if( "float".equals(name) ) sb.append(JVM_FLOAT);
			else if( "int".equals(name) ) sb.append(JVM_INT);
			else if( "long".equals(name) ) sb.append(JVM_LONG);
			else if( "short".equals(name) ) sb.append(JVM_SHORT);
			else sb.append('L').append(name).append(';'); // "java.lang.Object" ==> "Ljava.lang.Object;"
			name = sb.toString();
		}
		else
		{
			if( "void".equals(name) ) return void.class;
			else if( "boolean".equals(name) ) return boolean.class;
			else if( "byte".equals(name) ) return byte.class;
			else if( "char".equals(name) ) return char.class;
			else if( "double".equals(name) ) return double.class;
			else if( "float".equals(name) ) return float.class;
			else if( "int".equals(name) ) return int.class;
			else if( "long".equals(name) ) return long.class;
			else if( "short".equals(name) ) return short.class;
		}

		if( cl == null )
			cl = ClassHelper.getClassLoader();
		return Class.forName(name, true, cl);
	}

	/**
	 * desc to class.
	 * "[Z" => boolean[].class
	 * "[[Ljava/util/Map;" => java.util.Map[][].class
	 * 
	 * @param desc desc.
	 * @return Class instance.
	 * @throws ClassNotFoundException 
	 */
	public static Class<?> desc2class(String desc) throws ClassNotFoundException
	{
		return desc2class(ClassHelper.getClassLoader(), desc);
	}

	/**
	 * desc to class.
	 * "[Z" => boolean[].class
	 * "[[Ljava/util/Map;" => java.util.Map[][].class
	 * 
	 * @param cl ClassLoader instance.
	 * @param desc desc.
	 * @return Class instance.
	 * @throws ClassNotFoundException 
	 */
	public static Class<?> desc2class(ClassLoader cl, String desc) throws ClassNotFoundException
	{
		switch( desc.charAt(0) )
		{
			case JVM_VOID: return void.class;
			case JVM_BOOLEAN: return boolean.class;
			case JVM_BYTE: return byte.class;
			case JVM_CHAR: return char.class;
			case JVM_DOUBLE: return double.class;
			case JVM_FLOAT: return float.class;
			case JVM_INT: return int.class;
			case JVM_LONG: return long.class;
			case JVM_SHORT: return short.class;
			case 'L':
				desc = desc.substring(1, desc.length()-1).replace('/', '.'); // "Ljava/lang/Object;" ==> "java.lang.Object"
				break;
			case '[':
				desc = desc.replace('/', '.');  // "[[Ljava/lang/Object;" ==> "[[Ljava.lang.Object;"
				break;
			default:
				throw new ClassNotFoundException("Class not found: " + desc);
		}

		if( cl == null )
			cl = ClassHelper.getClassLoader();
		return Class.forName(desc, true, cl);
	}

	/**
	 * get class array instance.
	 * 
	 * @param desc desc.
	 * @return Class class array.
	 * @throws ClassNotFoundException 
	 */
	public static Class<?>[] desc2classArray(String desc) throws ClassNotFoundException
	{
		return desc2classArray(ClassHelper.getClassLoader(), desc);
	}

	/**
	 * get class array instance.
	 * 
	 * @param cl ClassLoader instance.
	 * @param desc desc.
	 * @return Class[] class array.
	 * @throws ClassNotFoundException 
	 */
	public static Class<?>[] desc2classArray(ClassLoader cl, String desc) throws ClassNotFoundException
	{
		if( desc.length() == 0 )
			return EMPTY_CLASS_ARRAY;

		List<Class<?>> cs = new ArrayList<Class<?>>();
		Matcher m = DESC_PATTERN.matcher(desc);
		while(m.find())
			cs.add(desc2class(cl, m.group()));
		return cs.toArray(EMPTY_CLASS_ARRAY);
	}

	/**
	 * 根据方法签名从类中找出方法。
	 * 
	 * @param clazz 查找的类。
	 * @param methodSignature 方法签名，形如method1(int, String)。也允许只给方法名不参数只有方法名，形如method2。
	 * @return 返回查找到的方法。
	 * @throws NoSuchMethodException
	 * @throws ClassNotFoundException  
	 * @throws IllegalStateException 给定的方法签名找到多个方法（方法签名中没有指定参数，又有有重载的方法的情况）
	 */
	public static Method findMethodByMethodSignature(Class<?> clazz, String methodName, String[] parameterTypes)
	        throws NoSuchMethodException, ClassNotFoundException {
        if (parameterTypes == null) {
            List<Method> finded = new ArrayList<Method>();
            for (Method m : clazz.getMethods()) {
                if (m.getName().equals(methodName)) {
                    finded.add(m);
                }
            }
            if (finded.isEmpty()) {
                throw new NoSuchMethodException("No such method " + methodName + " in class " + clazz);
            }
            if(finded.size() > 1) {
                String msg = String.format("Not unique method for method name(%s) in class(%s), find %d methods.",
                        methodName, clazz.getName(), finded.size());
                throw new IllegalStateException(msg);
            }
            return finded.get(0);
        } else {
            Class<?>[] types = new Class<?>[parameterTypes.length];
            for (int i = 0; i < parameterTypes.length; i ++) {
                types[i] = ReflectUtils.name2class(parameterTypes[i]);
            }
            return clazz.getMethod(methodName, types);
        }
	}

    public static Method findMethodByMethodName(Class<?> clazz, String methodName)
    		throws NoSuchMethodException, ClassNotFoundException {
    	return findMethodByMethodSignature(clazz, methodName, null);
    }
    
    public static Constructor<?> findConstructor(Class<?> clazz, Class<?> paramType) throws NoSuchMethodException {
    	Constructor<?> targetConstructor;
		try {
			targetConstructor = clazz.getConstructor(new Class<?>[] {paramType});
		} catch (NoSuchMethodException e) {
			targetConstructor = null;
			Constructor<?>[] constructors = clazz.getConstructors();
			for (Constructor<?> constructor : constructors) {
				if (Modifier.isPublic(constructor.getModifiers()) 
						&& constructor.getParameterTypes().length == 1
						&& constructor.getParameterTypes()[0].isAssignableFrom(paramType)) {
					targetConstructor = constructor;
					break;
				}
			}
			if (targetConstructor == null) {
				throw e;
			}
		}
		return targetConstructor;
    }
    
	private ReflectUtils(){}
}