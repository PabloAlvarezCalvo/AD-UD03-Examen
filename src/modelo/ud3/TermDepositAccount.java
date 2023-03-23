package modelo.ud3;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@Table(name="TERM_DEPOSIT_ACCOUNT")
@PrimaryKeyJoinColumn(name="account_id")
public class TermDepositAccount extends Account implements Serializable {
	private float interes;
	private int plazo_meses;
	
	public TermDepositAccount() {
		
	}
	
	
	public float getInteres() {
		return interes;
	}
	
	
	public void setInteres(float interes) {
		this.interes = interes;
	}
	
	
	public int getPlazo_meses() {
		return plazo_meses;
	}
	
	
	public void setPlazo_meses(int plazo_meses) {
		this.plazo_meses = plazo_meses;
	}
	
	
}
