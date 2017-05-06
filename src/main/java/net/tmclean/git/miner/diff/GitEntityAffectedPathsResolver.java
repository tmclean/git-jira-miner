package net.tmclean.git.miner.diff;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.util.io.DisabledOutputStream;

public class GitEntityAffectedPathsResolver 
{
	public List<String> resolve( Repository repo, String startRefName, String endRefName ) throws IOException
	{
		List<String> paths = new ArrayList<>();
		
    	ObjectId startId = resolveFromRef( repo, startRefName );
    	ObjectId endId = resolveFromRef( repo, endRefName );
		
		RevWalk walker = new RevWalk( repo );
		
		try
		{
			RevCommit startCommit = walker.parseCommit( startId );
			RevCommit endCommit = walker.parseCommit( endId );
			
			if( startCommit != null && endCommit != null )
			{
    			DiffFormatter df = new DiffFormatter( DisabledOutputStream.INSTANCE );
    			try
    			{
	    			df.setRepository( repo );
	    			df.setDiffComparator( RawTextComparator.DEFAULT );
	    			df.setDetectRenames( true );
	    			List<DiffEntry> diffs = df.scan( startCommit.getTree(), endCommit.getTree() );
	    			
	    			for( DiffEntry diff : diffs )
	    			{
	    			    if( !paths.contains( diff.getOldPath() ) )
	    			    {
	    			    	paths.add( diff.getOldPath() );
	    			    }
	    			    
	    			    if( !paths.contains( diff.getNewPath() ) )
	    			    {
	    			    	paths.add( diff.getNewPath() );
	    			    }
	    			}
    			}
    			finally
    			{
    				df.close();
    			}
			}
			
		}
		finally
		{
			walker.close();
		}
    	
    	return paths;
	}

	private ObjectId resolveFromRef( Repository repo, String refStr ) throws IOException
	{
		if( refStr == null || "".equals( refStr.trim() ) )
			return null;
		
    	Ref ref = repo.findRef( refStr );
    	ref = ref.getLeaf();
    	return ref.isPeeled() ? ref.getPeeledObjectId() : ref.getObjectId();
	}
}
