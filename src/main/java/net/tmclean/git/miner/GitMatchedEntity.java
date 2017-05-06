package net.tmclean.git.miner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GitMatchedEntity 
{
	private final String name;
	private final List<String> objectIds = new ArrayList<>( 1 );
	private final List<String> refs      = new ArrayList<>( 0 );

	private MatchedEntityStatus status = MatchedEntityStatus.NEW;
	
	public GitMatchedEntity( String name, String objectId )
	{
		this.name = name;
		this.objectIds.add( objectId );
	}
	
	public GitMatchedEntity( String name, String objectId, MatchedEntityStatus status )
	{
		this.name = name;
		this.objectIds.add( objectId );
		this.status = status;
	}
	
	public MatchedEntityStatus getStatus() { return status; }
	public void setStatus(MatchedEntityStatus status) { this.status = status; }

	public String getName() { return name; }
	
	public void addObjectId( String objectId )
	{
		objectIds.add( objectId );
	}
	
	public List<String> getObjectIds()
	{
		return Collections.unmodifiableList( objectIds );
	}
	
	public void addRef( String ref )
	{
		refs.add( ref );
	}
	
	public List<String> getRefs()
	{
		return Collections.unmodifiableList( refs );
	}
}
