package modelo.servicio;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import exceptions.SaldoInsuficienteException;
import modelo.ud3.AccMovement;
import modelo.ud3.Account;
import modelo.ud3.Departamento;
import modelo.ud3.Empleado;
import exceptions.InstanceNotFoundException;
import util.SessionFactoryUtil;

public class AccountServicio implements IAccountServicio {

	@Override
	public Account findAccountById(int accId) throws InstanceNotFoundException {
		SessionFactory sessionFactory = SessionFactoryUtil.getSessionFactory();
		Session session = sessionFactory.openSession();
		Account account = session.get(Account.class, accId);
		if (account == null) {
			throw new InstanceNotFoundException(Account.class.getName());
		}

		session.close();
		return account;
	}

	@Override
	public AccMovement transferir(int accOrigen, int accDestino, double cantidad)
			throws SaldoInsuficienteException, InstanceNotFoundException, UnsupportedOperationException {

		Transaction tx = null;
		Session session = null;
		AccMovement movement = null;

		try {

			if (cantidad <= 0) {
				throw new UnsupportedOperationException();
			}
			SessionFactory sessionFactory = SessionFactoryUtil.getSessionFactory();
			session = sessionFactory.openSession();

			Account accountOrigen = session.get(Account.class, accOrigen);
			if (accountOrigen == null) {
				throw new InstanceNotFoundException(Account.class.getName() + " origen id:" + accOrigen);
			}
			BigDecimal cantidadBD = new BigDecimal(cantidad);
			if (accountOrigen.getAmount().compareTo(cantidadBD) < 0) {
				throw new SaldoInsuficienteException("No hay saldo suficiente", accountOrigen.getAmount(), cantidadBD);
			}
			Account accountDestino = session.get(Account.class, accDestino);
			if (accountDestino == null) {
				throw new InstanceNotFoundException(Account.class.getName() + " destino id:" + accDestino);
			}

			tx = session.beginTransaction();

			accountOrigen.setAmount(accountOrigen.getAmount().subtract(cantidadBD));
			accountDestino.setAmount(accountDestino.getAmount().add(cantidadBD));

			movement = new AccMovement();
			movement.setAmount(cantidadBD);
			movement.setDatetime(LocalDateTime.now());

			// Relación bidireccional
			movement.setAccountOrigen(accountOrigen);
			movement.setAccountDestino(accountDestino);
			// Son prescindibles y no recomendables en navegación bidireccional porque una
			// Account puede tener numerosos movimientos
//					accountOrigen.getAccMovementsOrigen().add(movement);
//					accountDestino.getAccMovementsDest().add(movement);

//					session.saveOrUpdate(accountOrigen);
//					session.saveOrUpdate(accountDestino);
			session.save(movement);

			tx.commit();

		} catch (Exception ex) {
			System.out.println("Ha ocurrido una exception: " + ex.getMessage());
			if (tx != null) {
				tx.rollback();
			}
			throw ex;
		} finally {
			if (session != null) {
				session.close();
			}
		}

		return movement;

	}

	@Override
	public AccMovement autoTransferir(int accNo, double cantidad) throws InstanceNotFoundException {

		Transaction tx = null;
		Session session = null;
		AccMovement movement = null;

		try {
			SessionFactory sessionFactory = SessionFactoryUtil.getSessionFactory();
			session = sessionFactory.openSession();

			Account account = session.get(Account.class, accNo);
			if (account == null) {
				throw new InstanceNotFoundException(Account.class.getName() + " origen id:" + accNo);
			}
			BigDecimal cantidadBD = new BigDecimal(cantidad);

			tx = session.beginTransaction();

			account.setAmount(account.getAmount().add(cantidadBD));

			movement = new AccMovement();
			movement.setAmount(cantidadBD);
			movement.setDatetime(LocalDateTime.now());

			// Relación bidireccional
			movement.setAccountOrigen(account);
			movement.setAccountDestino(account);

			session.save(movement);

			tx.commit();

		} catch (Exception ex) {
			System.out.println("Ha ocurrido una exception: " + ex.getMessage());
			if (tx != null) {
				tx.rollback();
			}
			throw ex;
		} finally {
			if (session != null) {
				session.close();
			}
		}

		return movement;

	}


	public Account saveOrUpdate(Account d) {
		SessionFactory sessionFactory = SessionFactoryUtil.getSessionFactory();
		Session session = sessionFactory.openSession();
		Transaction tx = null;

		try {
			tx = session.beginTransaction();

			session.saveOrUpdate(d);
			tx.commit();
		} catch (Exception ex) {
			System.out.println("Ha ocurrido una excepción en saveOrUpdate Account: " + ex.getMessage());
			if (tx != null) {
				tx.rollback();
			}
			throw ex;
		} finally {
			session.close();
		}
		return d;
	}

	
	
	
	public boolean delete(int accId) throws InstanceNotFoundException {
		/*TODO Modificar
		 * 8. Modifica el proyecto para que la implementación del método
		 * public boolean delete(int accId) throws InstanceNotFoundException
		 * permita eliminar una cuenta y todos sus movimientos en una transacción. 
		 * Debes crear movimientos modificando previamente el importe de la cuenta a través de la interfaz gráfica.
		*/
		SessionFactory sessionFactory = SessionFactoryUtil.getSessionFactory();
		Session session = sessionFactory.openSession();
		Transaction tx = null;
		boolean exito = false;

		try {
			tx = session.beginTransaction();
			Account account = session.get(Account.class, accId);
			if (account != null) {
				
				
				//session.remove(account); //Original
				
				/*TODO
				 * 
				 * 
				 * 
				 * 
				 */
				/*
				List<Empleado> titulares = getTitularesByAccountId(account.getAccountno());
				account.getAmount();
				
				for (Empleado e : titulares) {
					e.getAccounts();
					e.getEname();
					e.getAccounts().remove(account);
					account.getEmployees().remove(e);
				}
				
				session.remove(account);
				*/
				
				//Aquí la modificación para persistir con Orphan Delete
				//No funciona, solo borra las cuentas con el único movimiento de creación
				
				for (AccMovement accMovs : account.getAccMovementsForAccountOriginId()) {
					accMovs.setAccountOrigen(null);
					accMovs.setAccountDestino(null);
					account.getAccMovementsForAccountOriginId().remove(accMovs);
					session.remove(accMovs);
				}
				
				for (AccMovement accMovs : account.getAccMovementsForAccountDestId()) {
					accMovs.setAccountOrigen(null);
					accMovs.setAccountDestino(null);
					account.getAccMovementsForAccountDestId().remove(accMovs);
					session.remove(accMovs);
				}
				
				Iterator<Empleado> iterador = account.getEmployees().iterator();
				
				while (iterador.hasNext()) {
					Empleado emp = iterador.next();
					emp.getAccounts().remove(account);
					account.getEmployees().remove(emp);
				}
				
				session.remove(account);
				
				
			} else {
				throw new InstanceNotFoundException(Account.class.getName() + " id: " + accId);
			}
			tx.commit();
			exito = true;
		} catch (Exception ex) {
			System.out.println("Ha ocurrido una excepción en delete Account: " + ex.getMessage());
			if (tx != null) {
				tx.rollback();
			}

			throw ex;
		} finally {
			session.close();
		}
		return exito;
	}

	@Override
	public List<Account> getAccountsByEmpno(int empno) {
		SessionFactory sessionFactory = SessionFactoryUtil.getSessionFactory();
		Session session = sessionFactory.openSession();
		List<Account> accounts = null;
		
		try {
			accounts = session.createQuery("SELECT e.accounts FROM Empleado e WHERE e.empno LIKE :id").setParameter("id", empno).list();
			
			if (accounts == null) {
				throw new InstanceNotFoundException(Account.class.getName());
			}
		} catch(Exception e) {
			System.out.println("Se ha producido una excepción en getAccountsByEmpno: " + e.getMessage());
		}
		

		session.close();
		return accounts;
	}

	@Override
	public List<Empleado> getTitularesByAccountId(int accId) throws InstanceNotFoundException {
		SessionFactory sessionFactory = SessionFactoryUtil.getSessionFactory();
		Session session = sessionFactory.openSession();
		
		//Desde extremo empleado
		//List<Empleado> empleados = session.createQuery("SELECT e FROM Empleado e JOIN e.accounts a WHERE a.accountno LIKE :id").setParameter("id", accId).list();
		//Desde extremo account, supongo que esto tiene más sentido en base a la info de búsqueda recibida
		List<Empleado> empleados = session.createQuery("SELECT e FROM Account a JOIN a.employees e WHERE a.accountno LIKE :id").setParameter("id", accId).list();
		if (empleados == null) {
			throw new InstanceNotFoundException(Account.class.getName());
		}

		session.close();
		return empleados;
	}

	@Override
	public Account addAccountToEmployee(int empno, Account acc) {
		//Para que cree una nueva cuenta asociada a un empleado. 
		//Asegúrate de crear la relación de forma bidireccional antes de guardar los cambios. 
		//Utiliza una transacción. (1,25 puntos)
		
		SessionFactory sessionFactory = SessionFactoryUtil.getSessionFactory();
		Session session = sessionFactory.openSession();
		Transaction tx = null;
		Empleado empleado = null;

		try {
			tx = session.beginTransaction();

			
			empleado = session.get(Empleado.class, empno);
			
			if (empleado != null) {
				empleado.addAccount(acc);
			}
			
			session.save(empleado);
			session.save(acc);
			
			tx.commit();
		} catch (Exception ex) {
			System.out.println("Ha ocurrido una excepción en addAccountToEmployee: " + ex.getMessage());
			if (tx != null) {
				tx.rollback();
			}
			throw ex;
		} finally {
			session.close();
		}
		return acc;
	}


}
