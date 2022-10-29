import java.util.Set;
import java.util.TreeSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * This utility class is not meant to be instantitated, and just provides some
 * useful methods on FD sets.
 * 
 * @author <<YOUR NAME>>
 * @version <<DATE>>
 */
public final class FDUtil {

  /**
   * Resolves all trivial FDs in the given set of FDs
   * 
   * @param fdset (Immutable) FD Set
   * @return a set of trivial FDs with respect to the given FDSet
   */
  public static FDSet trivial(final FDSet fdset) {
    // Set to hold generated trivial FDs.
	FDSet trivial = new FDSet();
	//find all subsets
	Set<FD> temp = fdset.getSet();
	//generate trivials for each FD already in the set
	for (FD fd : temp) {
		Set<Set<String>> left = new HashSet<Set<String>>();
		left.addAll(powerSet(fd.getLeft()));
		for (Set<String> set: left) {
			if (!set.isEmpty()) {
				FD trivFD = new FD();
				trivFD.addToLeft(fd.getLeft());
				trivFD.addToRight(set);
				trivial.add(trivFD);
			}
			
		}
	}
    return trivial;
  }


  /**
   * Augments every FD in the given set of FDs with the given attributes
   * 
   * @param fdset FD Set (Immutable)
   * @param attrs a set of attributes with which to augment FDs (Immutable)
   * @return a set of augmented FDs
   */
  public static FDSet augment(final FDSet fdset, final Set<String> attrs) {
    // TODO: Copy each FD in the given set and then union both sides with the given
    // set of attributes, and add this augmented FD to a new FDSet.
	FDSet augmented = new FDSet();
	Set<String> att = new HashSet(attrs);
	for (FD fd: fdset) {
		FD temp = new FD(fd);
		temp.addToLeft(att);
		temp.addToRight(att);
		augmented.add(temp);
	}
    return augmented;
  }

  /**
   * Exhaustively resolves transitive FDs with respect to the given set of FDs
   * 
   * @param fdset (Immutable) FD Set
   * @return all transitive FDs with respect to the input FD set
   */
  public static FDSet transitive(final FDSet fdset) {
    // TODO: Examine each pair of FDs in the given set. If the transitive property
    // holds on the pair of FDs, then generate the new FD and add it to a new FDSet.
    // Repeat until no new transitive FDs are found.
	boolean looking = true;
	FDSet newSet = new FDSet(fdset);
	FDSet newOnly = new FDSet();
	while (looking) {
	int initSize = newSet.size();
	FDSet comp = new FDSet(newSet);
	for (FD first: comp) {
		  for (FD second: comp) {
			  if (first.getRight().equals(second.getLeft())) {
				  FD newfd = new FD();
				  newfd.addToLeft(first.getLeft());
				  newfd.addToRight(second.getRight());
				  newSet.add(newfd);
				  newOnly.add(newfd);
			  }
		  }
	}
	if (newSet.size() == initSize) {
		  return newOnly;
	}
	else newSet.addAll(comp);
	}   
    return newOnly;
  }

  /**
   * Generates the closure of the given FD Set
   * 
   * @param fdset (Immutable) FD Set
   * @return the closure of the input FD Set
   */
  public static FDSet fdSetClosure(final FDSet fdset) {
	  
    // TODO: Use the FDSet copy constructor to deep copy the given FDSet
	FDSet closed = new FDSet(fdset);
	boolean looking = true;
	while(looking) {
		int setSize = closed.size();
		FDSet temp = new FDSet(closed);
		Set<String> collectedAtts = getAtts(closed);
		for (String att: collectedAtts) {
			Set<String> aux = new HashSet<String>();
			aux.add(att);
			for (FD fd:closed) {
				FD newFD = new FD(fd);
				newFD.addToLeft(aux);
				newFD.addToRight(aux);
				temp.add(newFD);
			}
		}
		temp.addAll(trivial(temp));
		temp.addAll(transitive(temp));
		//Add in the temp vals
		closed.addAll(temp);
		if (closed.size() == setSize) {
			looking = false;
		}
	}
      
    return closed;
  }
  
  public static <E> Set<String> getAtts(final FDSet check) {
	  Set<String> collectedAtts = new HashSet<String>();
	  for (FD fd: check) {
		  collectedAtts.addAll(fd.getLeft());
		  collectedAtts.addAll(fd.getRight());
	  }
	return collectedAtts;
  }
  

  /**
   * Generates the power set of the given set (that is, all subsets of
   * the given set of elements)
   * 
   * @param set Any set of elements (Immutable)
   * @return the power set of the input set
   */
  @SuppressWarnings("unchecked")
  public static <E> Set<Set<E>> powerSet(final Set<E> set) {

    // base case: power set of the empty set is the set containing the empty set
    if (set.size() == 0) {
      Set<Set<E>> basePset = new HashSet<>();
      basePset.add(new HashSet<>());
      return basePset;
    }

    // remove the first element from the current set
    E[] attrs = (E[]) set.toArray();
    set.remove(attrs[0]);

    // recurse and obtain the power set of the reduced set of elements
    Set<Set<E>> currentPset = FDUtil.powerSet(set);

    // restore the element from input set
    set.add(attrs[0]);

    // iterate through all elements of current power set and union with first
    // element
    Set<Set<E>> otherPset = new HashSet<>();
    for (Set<E> attrSet : currentPset) {
      Set<E> otherAttrSet = new HashSet<>(attrSet);
      otherAttrSet.add(attrs[0]);
      otherPset.add(otherAttrSet);
    }
    currentPset.addAll(otherPset);
    return currentPset;
  }
  
  
}