package events;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.Element;

import com.sun.source.doctree.DocTree;

import jdk.javadoc.doclet.Taglet;

/**
 * EventTaglet is used to create the event tags in the javadoc.
 * 
 * @version 0.1
 * @author group17
 */
public class EventTaglet implements Taglet {

	 private static final String NAME = "event";
	    private static final String HEADER = "Events:";

	    
	    
	    /**
	     * Return the name of this custom tag.
	     */
	    public String getName() {
	        return NAME;
	    }


	    /**
	     * Will return false since <code>@event</code>
	     * is not an inline tag.
	     * @return false since <code>@event</code>
	     * is not an inline tag.
	     */

	    public boolean isInlineTag() {
	        return false;
	    }
	   
	    /**
	     * Retrieve the allowed Locations to show the taglet.
	     * @return A set with all the locations.
	     */
		@Override
		public Set<Location> getAllowedLocations() {
			Set<Location> l = new HashSet<Taglet.Location>();
			
			l.add(Location.CONSTRUCTOR);
			l.add(Location.METHOD);
			return l;
		}

		/**
	     * Register this Taglet.
	     * @param tagletMap  the map to register this tag to.
	     */
	    public static void register(Map<String, EventTaglet> tagletMap) {
	       EventTaglet tag = new EventTaglet();
	       Taglet t = tagletMap.get(tag.getName());
	       if (t != null) {
	           tagletMap.remove(tag.getName());
	       }
	       tagletMap.put(tag.getName(), tag);
	    }
		
	    /**
	     * Given an list of <code>Event</code>s representing this custom
	     * tag, return its string representation.
	     * @param tags  the list of <code>Event</code>s representing of this custom tag.
	     */
		@Override
		public String toString(List<? extends DocTree> tags, Element element) {
	        if (tags.size() == 0) {
	            return null;
	        }
	        String result = "\n<DT><B>" + HEADER + "</B>";
	        for (int i = 0; i < tags.size(); i++) {
	        	result += "<DD>";
	        	String event=tags.get(i).toString().trim();
	        	event = event.replaceAll("(@event)\\s+(\\w+)\\s+(.*)", "<code>$2</code> - $3");
	            result += event;
	            result += "</DD>";
	        }
	        
	        return result + "\n";
		}

}
