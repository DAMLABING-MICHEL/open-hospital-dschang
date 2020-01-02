package org.isf.mortuary.service;

	import java.sql.ResultSet;
	import java.sql.SQLException;
	import java.util.ArrayList;
	import java.util.Collections;
	import java.util.GregorianCalendar;
	import java.util.Iterator;
	import java.util.List;

	import org.isf.generaldata.MessageBundle;
	import org.isf.menu.gui.MainMenu;
	import org.isf.mortuary.model.DeathReason;
	import org.isf.mortuary.model.IntervalJours;
	import org.isf.mortuary.model.PlagePrixMorgue;
	import org.isf.utils.db.DbQueryLogger;
	import org.isf.utils.exception.OHException;
	import org.isf.utils.time.TimeTools;

	/**
	 * Persistence class for the Death Reason Types.
	 */


public class PriceIoOperations {
		/**
		 * Returns all the stored {@link PlagePrixMorgue}s.
		 * @return a list of PlagePrixMorgue.
		 * @throws OHException if an error occurs retrieving the PlagePrixMorgue list.
		 */
		public ArrayList<PlagePrixMorgue> getPrices() throws OHException {
			ArrayList<PlagePrixMorgue> prices = null;
			String query = "select * from PLAGE_PRIX_MORGUE ORDER BY NB_JOUR_MIN ASC";
			DbQueryLogger dbQuery = new DbQueryLogger();
			try{
				ResultSet resultSet = dbQuery.getData(query,true);
				prices = new ArrayList<PlagePrixMorgue>(resultSet.getFetchSize());
			
				while (resultSet.next()) {
					prices.add(new PlagePrixMorgue(resultSet.getInt("PLAGE_PRIX_ID"), resultSet.getInt("NB_JOUR_MIN"), resultSet.getInt("NB_JOUR_MAX"), resultSet.getFloat("PRIX_JOURNALIER"), resultSet.getString("DESCRIPTION"), resultSet.getString("CODE")));
				}
				
			} catch (SQLException e) {
				throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
			} finally{
				dbQuery.releaseConnection();
			}
			return prices;
		}

		/**
		 * Updates the specified {@link PlagePrixMorgue}.
		 * @param price to update.
		 * @return <code>true</code> if price has been updated, false otherwise.
		 * @throws OHException if an error occurs during the update operation.
		 */
		public boolean updatePrice(PlagePrixMorgue price) throws OHException {
			DbQueryLogger dbQuery = new DbQueryLogger();
			boolean result = false;
			try {
				List<Object> parameters = new ArrayList<Object>();
				parameters.add(price.getId());
				parameters.add(price.getNbJourmin());
				parameters.add(price.getNbJourMax());
				parameters.add(price.getDescription());
				parameters.add(price.getCode());
				parameters.add(MainMenu.getUser());
				parameters.add(new java.sql.Timestamp(TimeTools.getServerDateTime().getTime().getTime()));
				
				parameters.add(price.getId());
				String query = "update PLAGE_PRIX_MORGUE set PLAGE_PRIX_ID=?, "
						+ "NB_JOUR_MIN=?, "
						+ "NB_JOUR_MAX=?, "
						+ "DESCRIPTION=?, "
						+ "CODE=?, "
						+ "PLAGE_MODIFY_BY=?, "
						+ "PLAGE_MODIFY_DATE=? where PLAGE_PRIX_ID=?";
				result = dbQuery.setDataWithParams(query, parameters, true);
				
			} finally{
				dbQuery.releaseConnection();
			}
			return result;
		}

		/**
		 * Store the specified {@link PlagePrixMorgue}.
		 * @param price the price to store.
		 * @return <code>true</code> if the {@link PlagePrixMorgue} has been stored, <code>false</code> otherwise.
		 * @throws OHException if an error occurs during the store operation.
		 */
		public boolean newPrice(PlagePrixMorgue price) throws OHException {
			DbQueryLogger dbQuery = new DbQueryLogger();
			boolean result = false;
			try {
				List<Object> parameters = new ArrayList<Object>(6);
				//parameters.add(price.getId());
				parameters.add(price.getNbJourmin());
				parameters.add(price.getNbJourMax());
				parameters.add(price.getDescription());
				parameters.add(price.getCode());
				parameters.add(MainMenu.getUser());
				parameters.add(new java.sql.Timestamp(TimeTools.getServerDateTime().getTime().getTime()));
				
				String query = "insert into PLAGE_PRIX_MORGUE (NB_JOUR_MIN, NB_JOUR_MAX, DESCRIPTION, CODE, PLAGE_CREATE_BY, PLAGE_CREATE_DATE) values (?, ?, ?, ?, ?, ?)";
				result = dbQuery.setDataWithParams(query, parameters, true);
			} finally{
				dbQuery.releaseConnection();
			}
			return result;
		}
		
		/**
		 * Deletes the specified {@link PlagePrixMorgue}.
		 * @param id the id of the  PlagePrixMorgue to remove.
		 * @return <code>true</code> if the dPlagePrixMorgue has been removed, <code>false</code> otherwise.
		 * @throws OHException if an error occurs during the delete procedure.
		 */
		public boolean deletePrice(int id) throws OHException {
			DbQueryLogger dbQuery = new DbQueryLogger();
			boolean result = false;
			try {
				List<Object> parameters = Collections.<Object>singletonList(id);
				String query = "delete from PLAGE_PRIX_MORGUE where PLAGE_PRIX_ID = ?";
				result = dbQuery.setDataWithParams(query, parameters, true);
			} finally{
				dbQuery.releaseConnection();
			}
			return result;
		}

		/**
		 * Checks if the specified min days, max days, can be insert in {@link PLAGE_PRIX_MORGUE}.
		 * @param min the min numbers of days
		 * @param max the max number of days
		 * @return <code>true</code> if the code is used, false otherwise.
		 * @throws OHException if an error occurs during the check.
		 */
		public boolean isPriceRangeCoherent(int id, int min, int max) throws OHException{
			DbQueryLogger dbQuery = new DbQueryLogger();
			boolean coherent=true;
			List<IntervalJours> list = new ArrayList<IntervalJours>();
			try {
				List<Object> parameters = Collections.<Object>singletonList(id);
				String query = "SELECT * FROM PLAGE_PRIX_MORGUE where PLAGE_PRIX_ID != ? ORDER BY NB_JOUR_MIN ASC";
				ResultSet set = dbQuery.getDataWithParams(query, parameters, true);
				
				while(set.next()) {					
					list.add(new IntervalJours(set.getInt("NB_JOUR_MIN"), set.getInt("NB_JOUR_MAX")));
				}
			} catch (SQLException e) {
				throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
			} finally{
				dbQuery.releaseConnection();
			}
			int j = 0;
			while(j < list.size()) {
				
				IntervalJours inter = list.get(j);
				int minval = inter.getMinjours();
				int maxval = inter.getMaxjours();

				if(max < minval ) {coherent = true; break;}
				
				if(max <= maxval || min <= maxval) {
					coherent = false; break;
				}
				j++;				
			}
			if(j == list.size())  {coherent = true;}
				
			return coherent;
		}
		
		/**
		 * Get the PlagePrixMorgue by id.
		 * @param id the id of PlagePrixMorgue to search.
		 * @return <code>PlagePrixMorgue</code>.
		 * @throws OHException if an error occurs during the check.
		 */
		public PlagePrixMorgue getPriceById(int id) throws OHException{
			DbQueryLogger dbQuery = new DbQueryLogger();
			PlagePrixMorgue price= null;
			try{
				List<Object> parameters = Collections.<Object>singletonList(id);
				String query = "SELECT * FROM PLAGE_PRIX_MORGUE where PLAGE_PRIX_ID = ?";
				ResultSet res = dbQuery.getDataWithParams(query, parameters, true);
				while(res.next()) {
					price = new PlagePrixMorgue(res.getInt("PLAGE_PRIX_ID"), res.getInt("NB_JOUR_MIN"), res.getInt("NB_JOUR_MAX"), res.getFloat("PRIX_JOURNALIER"), res.getString("DESCRIPTION"), res.getString("CODE"));
				}
			} catch (SQLException e) {
				throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
			} finally{
				dbQuery.releaseConnection();
			}
			return price;
		}

		public int dateDiff(GregorianCalendar dateEntree, GregorianCalendar dateSortie) throws OHException {
			DbQueryLogger dbQuery = new DbQueryLogger();
			int diff = 0;
			try{
				List<Object> parameters = new ArrayList<Object>(2);
				parameters.add(new java.sql.Timestamp(dateEntree.getTime().getTime()));
				parameters.add(new java.sql.Timestamp(dateSortie.getTime().getTime()));
				String query = "SELECT DATEDIFF(?, ?) AS DIFF";
				ResultSet res = dbQuery.getDataWithParams(query, parameters, true);	
				while(res.next())diff = res.getInt("DIFF");
			} catch (SQLException e) {
				throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
			} finally{
				dbQuery.releaseConnection();
			}
			return diff;
		}
		
}

	



