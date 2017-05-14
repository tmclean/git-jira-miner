package net.tmclean.git.miner.walker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

public class GitRepoWalker 
{
	private final Repository repo;
	private final Map<String, List<String>> reverseRefMap;
	
	public GitRepoWalker( Repository repo )
	{
		this.repo = repo;
    	this.reverseRefMap = buildReverseRefMap( repo );
	}

	public void walk( String start, GitWalkListener ... listeners ) throws IOException
	{
		walk( start, null, listeners );
	}
	
	public void walk( String start, String end, GitWalkListener ... listeners ) throws IOException
	{

    	RevWalk revWalk = new RevWalk( repo );
    	
    	try
    	{
        	ObjectId startId = resolveFromRef( repo, start );
	    	RevCommit startCommit = revWalk.lookupCommit( startId );
	    	
        	ObjectId endId = resolveFromRef( repo, end );
	    	RevCommit endCommit = null;
	    	
	    	if( endId != null )
	    		endCommit = revWalk.lookupCommit( endId );
    		
	    	revWalk.markStart( startCommit );
	    	
	    	RevCommit commit = null;
	    	while( (commit = revWalk.next()) != null )
	    	{
	    		for( GitWalkListener listener : listeners )
	    		{
	    			List<String> commitAliases = 
	    				this.reverseRefMap.containsKey( commit.getName() ) ? 
	    					this.reverseRefMap.get( commit.getName() ) : 
    						new ArrayList<>( 0 );
	    			
	    			listener.onCommit( commit, commitAliases );
	    		}
	    		
	    		if( isEndCommit( commit, endCommit ) )
	    			break;
	    	}
    	}
    	finally
    	{
    		revWalk.close();
    	}
	}

    private Map<String, List<String>> buildReverseRefMap( Repository repo )
    {
    	Map<String, Ref> refMap = repo.getAllRefs();
    	Map<String, List<String>> reverseRefMap = new HashMap<>();
    	
    	for( Entry<String, Ref> ref : refMap.entrySet() )
    	{
    		String id = ref.getValue().getTarget().getLeaf().getObjectId().getName();
    		
    		if( !reverseRefMap.containsKey( id ) )
    		{
    			reverseRefMap.put( id, new ArrayList<>() );
    		}
    		
    		reverseRefMap.get( id ).add( ref.getKey() );
    	}
    	
    	return reverseRefMap;
    }
	
	private ObjectId resolveFromRef( Repository repo, String refStr ) throws IOException
	{
		if( refStr == null || "".equals( refStr.trim() ) )
			return null;
		
    	Ref ref = repo.findRef( refStr );
    	ref = ref.getLeaf();
    	return ref.isPeeled() ? ref.getPeeledObjectId() : ref.getObjectId();
	}

	private boolean isEndCommit( RevCommit commit, RevCommit endCommit )
	{
		if( endCommit == null )
			return false;
		
		return commit.getName().equals( endCommit.getName() );
	}
}
