package org.reflectiveutils.javatype;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;


/**
 * Disclaimer: toString, equals and hashCode methods are adapted from sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl
 * (this implementation was not used since it is internal to a VM implementation)
 * @author sergioc78
 *
 */
public class ParameterizedTypeImpl implements ParameterizedType {

	private Type[] actualTypeArguments;
	private Type ownerType;
	private Class rawType;

	public ParameterizedTypeImpl(Type[] actualTypeArguments, Type ownerType,
			Class rawType) {
		this.actualTypeArguments = actualTypeArguments;
		this.ownerType = ownerType;
		this.rawType = rawType;
	}

	public Type[] getActualTypeArguments() {
		return actualTypeArguments;
	}

	public void setActualTypeArguments(Type[] actualTypeArguments) {
		this.actualTypeArguments = actualTypeArguments;
	}

	public Type getOwnerType() {
		return ownerType;
	}

	public void setOwnerType(Type ownerType) {
		this.ownerType = ownerType;
	}

	public Class getRawType() {
		return rawType;
	}

	public void setRawType(Class rawType) {
		this.rawType = rawType;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		if (ownerType != null) {
			if (ownerType instanceof Class)
				sb.append(((Class) ownerType).getName());
			else
				sb.append(ownerType.toString());

			sb.append(".");

			if (ownerType instanceof ParameterizedType
					&& ((ParameterizedType) ownerType).getRawType() instanceof Class) {
				// Find simple name of nested type by removing the
				// shared prefix with owner.
				sb.append(rawType.getName().replace(
						((Class) ((ParameterizedType) ownerType).getRawType())
								.getName() + "$", ""));
			} else
				sb.append(rawType.getName());
		} else
			sb.append(rawType.getName());

		if (actualTypeArguments != null && actualTypeArguments.length > 0) {
			sb.append("<");
			boolean first = true;
			for (Type t : actualTypeArguments) {
				if (!first)
					sb.append(", ");
				if (t instanceof Class)
					sb.append(((Class) t).getName());
				else
					sb.append(t.toString());
				first = false;
			}
			sb.append(">");
		}

		return sb.toString();
	}


	/*
	 * From the JavaDoc for java.lang.reflect.ParameterizedType "Instances of
	 * classes that implement this interface must implement an equals() method
	 * that equates any two instances that share the same generic type
	 * declaration and have equal type parameters."
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof ParameterizedType) {
			// Check that information is equivalent
			ParameterizedType that = (ParameterizedType) o;

			if (this == that)
				return true;

			Type thatOwner = that.getOwnerType();
			Type thatRawType = that.getRawType();

			if (false) { // Debugging
				boolean ownerEquality = (ownerType == null ? thatOwner == null
						: ownerType.equals(thatOwner));
				boolean rawEquality = (rawType == null ? thatRawType == null
						: rawType.equals(thatRawType));

				boolean typeArgEquality = Arrays.equals(actualTypeArguments, // avoid
																				// clone
						that.getActualTypeArguments());
				for (Type t : actualTypeArguments) {
					System.out.printf("\t\t%s%s%n", t, t.getClass());
				}

				System.out.printf("\towner %s\traw %s\ttypeArg %s%n",
						ownerEquality, rawEquality, typeArgEquality);
				return ownerEquality && rawEquality && typeArgEquality;
			}

			return (ownerType == null ? thatOwner == null : ownerType
					.equals(thatOwner))
					&& (rawType == null ? thatRawType == null : rawType
							.equals(thatRawType))
					&& Arrays.equals(actualTypeArguments, // avoid clone
							that.getActualTypeArguments());
		} else
			return false;
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(actualTypeArguments)
				^ (ownerType == null ? 0 : ownerType.hashCode())
				^ (rawType == null ? 0 : rawType.hashCode());
	}


}
