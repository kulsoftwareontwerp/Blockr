package types;

import java.io.*;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import com.kuleuven.swop.group17.GameWorldApi.Action;
import com.kuleuven.swop.group17.GameWorldApi.Predicate;

//Source: https://dzone.com/articles/enum-tricks-dynamic-enums

public class DynaEnum<E extends DynaEnum<E>> {
	private static Map<Class<? extends DynaEnum<?>>, Map<String, DynaEnum<?>>> elements = 
		new LinkedHashMap<Class<? extends DynaEnum<?>>, Map<String, DynaEnum<?>>>();
	
    private final String type;

    public final String type() {
    	return type;
    }

    final BlockCategory cat;

    public final BlockCategory cat() {
    	return cat;
    }
    
    final Action action;

    public final Action action() {
    	return action;
    }
    
    final Predicate predicate;

    public final Predicate predicate() {
    	return predicate;
    }
    
   private final String definitionBlockID;
   public final String definition() {
   	return definitionBlockID;
   }

	protected DynaEnum(String type, BlockCategory cat, Action action, Predicate predicate, String definition) {
		this.type = type;
		this.cat = cat;
		this.predicate = predicate;
		this.action = action;
		if(cat!= BlockCategory.CALL) {
			definition=null;
		}
		this.definitionBlockID=definition;
		Map<String, DynaEnum<?>> typeElements = elements.get(getClass());
		if (typeElements == null) {
			typeElements = new LinkedHashMap<String, DynaEnum<?>>();
			elements.put(getDynaEnumClass(), typeElements);
		}
		typeElements.put(type, this);
	}
	
	@SuppressWarnings("unchecked")
	private Class<? extends DynaEnum<?>> getDynaEnumClass() {
		return (Class<? extends DynaEnum<?>>)getClass();
	}

    @Override
	public String toString() {
    	return type;
    }
    
    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cat == null) ? 0 : cat.hashCode());
		result = prime * result + ((definitionBlockID == null) ? 0 : definitionBlockID.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DynaEnum other = (DynaEnum) obj;
		if (cat != other.cat)
			return false;
		if (definitionBlockID == null) {
			if (other.definitionBlockID != null)
				return false;
		} else if (!definitionBlockID.equals(other.definitionBlockID))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

    

    @Override
	protected final Object clone() throws CloneNotSupportedException {
    	throw new CloneNotSupportedException();
    }

//    public final int compareTo(E other) {
//		DynaEnum<?> self = this;
//		if (self.getClass() != other.getClass() && // optimization
//	            self.getDeclaringClass() != other.getDeclaringClass())
//		    throw new ClassCastException();
//		return self.ordinal - other.ordinal;
//    }


	@SuppressWarnings("unchecked")
	public final Class<E> getDeclaringClass() {
		Class clazz = getClass();
		Class zuper = clazz.getSuperclass();
		return (zuper == DynaEnum.class) ? clazz : zuper;
    }

    @SuppressWarnings("unchecked")
	public static <T extends DynaEnum<T>> T valueOf(Class<T> enumType, String type) {
    	return (T)elements.get(enumType).get(type);
    }

    @SuppressWarnings("unused")
	private void readObject(ObjectInputStream in) throws IOException,
        ClassNotFoundException {
            throw new InvalidObjectException("can't deserialize enum");
    }

    @SuppressWarnings("unused")
	private void readObjectNoData() throws ObjectStreamException {
        throw new InvalidObjectException("can't deserialize enum");
    }

    @Override
	protected final void finalize() { }

    
    public static <E> DynaEnum<? extends DynaEnum<?>>[] values() {
    	throw new IllegalStateException("Sub class of DynaEnum must implement method valus()");
    }
    
    @SuppressWarnings("unchecked")
	public static <E> E[] values(Class<E> enumType) {
    	Collection<DynaEnum<?>> values =  elements.get(enumType).values();
    	int n = values.size();
    	E[] typedValues = (E[])Array.newInstance(enumType, n);
    	int i = 0;
    	for (DynaEnum<?> value : values) {
    		Array.set(typedValues, i, value);
    		i++;
    	}
    	
    	return typedValues;
    }
}
