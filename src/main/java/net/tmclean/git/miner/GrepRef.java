package net.tmclean.git.miner;

public class GrepRef 
{
	private String name;
	private String id;
	
	public GrepRef() {}

	public GrepRef( String name, String id ) 
	{
		this.id = id;
		this.name = name;
	}

	public String getName() { return name; }
	public void setName(String name) { this.name = name; }

	public String getId() { return id; }
	public void setId(String id) { this.id = id; }
}
