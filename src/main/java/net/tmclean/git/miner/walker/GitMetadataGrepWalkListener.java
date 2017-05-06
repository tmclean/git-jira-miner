package net.tmclean.git.miner.walker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jgit.revwalk.RevCommit;

import net.tmclean.git.miner.GitMatchedEntity;

public class GitMetadataGrepWalkListener implements GitWalkListener 
{
	private final Map<String, GitMatchedEntity> matches = new HashMap<>();
	
	private final Pattern pattern;
	
	public GitMetadataGrepWalkListener( Pattern pattern )
	{
		this.pattern = pattern;
	}
	
	@Override
	public void onCommit( RevCommit commit, List<String> refAliases ) 
	{
    	String commitName = commit.getName();
    	
		for( String matchStr : extractMatches( pattern, commit.getFullMessage() ) )
		{
			if( !matches.containsKey( matchStr ) )
				matches.put( matchStr, new GitMatchedEntity( matchStr, commitName ) );
			else
				matches.get( matchStr ).addObjectId( commitName );
		}

		for( String ref : refAliases )
		{
			List<String> refMatches = extractMatches( pattern, ref );
			
			for( String matchStr : refMatches )
			{
				if( !matches.containsKey( matchStr ) )
				{
					GitMatchedEntity entity = new GitMatchedEntity( matchStr, commitName );
					matches.put( matchStr, entity );
				}
				
				matches.get( matchStr ).addRef( ref );
			}
		}
	}
    
    private List<String> extractMatches( Pattern pattern, String source )
    {
    	List<String> matches = new ArrayList<>();

		Matcher matcher = pattern.matcher( source );

		while( matcher.find() )
		{
			for( int i=0; i<matcher.groupCount(); i++ )
			{
				String match = matcher.group( 0 );
				if( !matches.contains( match ) )
					matches.add( match );
			}
		}
		
		return matches;
    }
    
    public Map<String, GitMatchedEntity> getMatches()
    {
    	return this.matches;
    }
}
