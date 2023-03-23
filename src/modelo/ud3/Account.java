package modelo.ud3;
// Generated 18:24:25, 23 de mar. de 2023 by Hibernate Tools 4.3.6.Final

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Account generated by hbm2java
 */
@Entity
@Table(name = "ACCOUNT", catalog = "empresa_prueba_ud3")
public class Account implements java.io.Serializable {

	private Integer accountno;
	private BigDecimal amount;
	private Set<Empleado> employees = new HashSet<Empleado>(0);
	private Set<AccMovement> accMovementsForAccountDestId = new HashSet<AccMovement>(0);
	private Set<AccMovement> accMovementsForAccountOriginId = new HashSet<AccMovement>(0);

	public Account() {
	}

	public Account(BigDecimal amount) {
		this.amount = amount;
	}

	public Account(BigDecimal amount, Set<Empleado> employees, Set<AccMovement> accMovementsForAccountDestId,
			Set<AccMovement> accMovementsForAccountOriginId) {
		this.amount = amount;
		this.employees = employees;
		this.accMovementsForAccountDestId = accMovementsForAccountDestId;
		this.accMovementsForAccountOriginId = accMovementsForAccountOriginId;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)

	@Column(name = "ACCOUNTNO", unique = true, nullable = false)
	public Integer getAccountno() {
		return this.accountno;
	}

	public void setAccountno(Integer accountno) {
		this.accountno = accountno;
	}

	@Column(name = "AMOUNT", nullable = false, scale = 4)
	public BigDecimal getAmount() {
		return this.amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "accounts")
	public Set<Empleado> getEmployees() {
		return this.employees;
	}

	public void setEmployees(Set<Empleado> employees) {
		this.employees = employees;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "accountByAccountDestId")
	public Set<AccMovement> getAccMovementsForAccountDestId() {
		return this.accMovementsForAccountDestId;
	}

	public void setAccMovementsForAccountDestId(Set<AccMovement> accMovementsForAccountDestId) {
		this.accMovementsForAccountDestId = accMovementsForAccountDestId;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "accountByAccountOriginId")
	public Set<AccMovement> getAccMovementsForAccountOriginId() {
		return this.accMovementsForAccountOriginId;
	}

	public void setAccMovementsForAccountOriginId(Set<AccMovement> accMovementsForAccountOriginId) {
		this.accMovementsForAccountOriginId = accMovementsForAccountOriginId;
	}

}
