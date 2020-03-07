package applicationLayer;

public interface DomainSubject {

	/**
	 * 
	 * @param listener
	 */
	void addDomainListener(DomainListener listener);

	/**
	 * 
	 * @param listener
	 */
	void removeDomainListener(DomainListener listener);

}