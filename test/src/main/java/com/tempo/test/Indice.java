package com.tempo.test;

import com.fasterxml.jackson.annotation.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Indice 
{
	private int ID;
	private String PAYS;
	private String NOM;
	
	@JsonCreator
	public Indice(@JsonProperty("ID") int ID, @JsonProperty("PAYS") String PAYS, @JsonProperty("NOM") String NOM)
	{
		this.ID = ID;
		this.PAYS = PAYS;
		this.NOM = NOM;
	}
	
	public Indice() {}
	
	public int getID() {return this.ID;}
	public String getPAYS() {return this.PAYS;}
	public String getNOM() {return this.NOM;}
	
	public void setID(int ID) {this.ID=ID;}
	public void setPAYS(String PAYS) {this.PAYS=PAYS;}
	public void setNOM(String NOM) {this.NOM=NOM;}
	
	 @Override
	 public String toString() {
	  return 
			 "ID : " + this.ID +
			 "PAYS : " + this.PAYS +
			 "NOM : " + this.NOM
			 ;
	 }
}
