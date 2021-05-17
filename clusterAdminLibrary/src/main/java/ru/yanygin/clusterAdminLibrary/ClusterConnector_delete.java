package ru.yanygin.clusterAdminLibrary;

import java.util.List;
import java.util.UUID;

import com._1c.v8.ibis.admin.AgentAdminException;
import com._1c.v8.ibis.admin.IAgentAdminConnection;
import com._1c.v8.ibis.admin.IClusterInfo;
import com._1c.v8.ibis.admin.IInfoBaseConnectionShort;
import com._1c.v8.ibis.admin.IInfoBaseInfo;
import com._1c.v8.ibis.admin.IInfoBaseInfoShort;
import com._1c.v8.ibis.admin.ISessionInfo;
import com._1c.v8.ibis.admin.client.IAgentAdminConnector;
import com._1c.v8.ibis.admin.client.IAgentAdminConnectorFactory;

/**
 * Utility class for interaction with the administration server of 1C:Enterprise 
 * server cluster
 */
public final class ClusterConnector_delete
{
	private final IAgentAdminConnectorFactory factory;

	private IAgentAdminConnector connector;
	private IAgentAdminConnection connection;

	public ClusterConnector_delete(IAgentAdminConnectorFactory factory)
	{
		this.factory = factory;
	}

    /**
	 * Establishes connection with the administration server of 1C:Enterprise 
     * server cluster
	 *
     * @param address server address
     * @param port IP port
     * @param timeout connection timeout (in milliseconds)
     *
     * @throws AgentAdminException in the case of errors.
	 */
	public void connect(String address, int port, long timeout)
	{
        if (connection != null)
        {
            throw new IllegalStateException("The connection is already established.");
        }

	    connector = factory.createConnector(timeout);
	    connection = connector.connect(address, port);
	}

	/**
	 * Checks whether connection to the administration server is established
	 *
	 * @return {@code true} if connected, {@code false} overwise
	 */
	public boolean isConnected()
	{
		return connection != null;
	}

	/**
	 * Terminates connection to the administration server
     *
     * @throws AgentAdminException in the case of errors.
	 */
	public void disconnect()
	{
        if (connection == null)
        {
            throw new IllegalStateException("The connection is not established.");
        }

        try
        {
	        connector.shutdown();
        }
        finally
        {
	        connection = null;
	        connector = null;
        }
	}
	
	/**
	 * Performs cluster authentication
	 * 
	 * @param clusterId cluster ID
	 * @param userName cluster administrator name
	 * @param password cluster administrator password
	 */
	public void authenticateCluster(UUID clusterId, String userName, String password)
	{
		if (connection == null)
		{
			throw new IllegalStateException("The connection is not established.");
		}

		connection.authenticate(clusterId, userName, password);
	}
	
	public void authenticateAgent(String userName, String password)
	{
		if (connection == null)
		{
			throw new IllegalStateException("The connection is not established.");
		}

		connection.authenticateAgent(userName, password);
	}
	
	/**
     * Adds infobase authentication parameters to the context 
     * of the current administration server connection
	 * 
	 * @param clusterId cluster ID
	 * @param userName infobase administrator name
	 * @param password infobase administrator password
	 */
    public void addInfoBaseCredentials(UUID clusterId, String userName, String password)
	{
		if (connection == null)
		{
			throw new IllegalStateException("The connection is not established.");
		}

		connection.addAuthentication(clusterId, userName, password);
	}
	
    /**
     * Gets the list of cluster descriptions
     *
     * @return list of cluster descriptions
     */
    public List<IClusterInfo> getClusterInfoList()
    {
		if (connection == null)
		{
			throw new IllegalStateException("The connection is not established.");
		}

        return connection.getClusters();
    }
    
    public UUID regCluster(IClusterInfo clusterInfo)
    {
		if (connection == null)
		{
			throw new IllegalStateException("The connection is not established.");
		}

        return connection.regCluster(clusterInfo);
    }
    
    public IClusterInfo getClusterInfo(UUID clusterId)
    {
		if (connection == null)
		{
			throw new IllegalStateException("The connection is not established.");
		}

        return connection.getClusterInfo(clusterId);
    }
    
    /**
     * Gets the list of short descriptions of cluster infobases
     *
     * @param clusterId cluster ID
     * @return list of short descriptions of cluster infobases
     */
    public List<IInfoBaseInfoShort> getInfoBasesShort(UUID clusterId)
    {
		if (connection == null)
		{
			throw new IllegalStateException("The connection is not established.");
		}

    	return connection.getInfoBasesShort(clusterId);
    }
    
    public List<IInfoBaseInfo> getInfoBases(UUID clusterId)
    {
		if (connection == null)
		{
			throw new IllegalStateException("The connection is not established.");
		}

    	return connection.getInfoBases(clusterId);
    }
    
    /**
	 * Gets the full infobase description
	 *
	 * @param clusterId cluster ID
	 * @param infoBaseId infobase ID
	 * @return infobase full infobase description
	 */
	public IInfoBaseInfo getInfoBaseInfo(UUID clusterId, UUID infoBaseId)
	{
		if (connection == null)
		{
			throw new IllegalStateException("The connection is not established.");
		}
		
		return connection.getInfoBaseInfo(clusterId, infoBaseId);
	}
	
	public IInfoBaseInfoShort getInfoBaseShortInfo(UUID clusterId, UUID infoBaseId)
	{
		if (connection == null)
		{
			throw new IllegalStateException("The connection is not established.");
		}
		
		return connection.getInfoBaseShortInfo(clusterId, infoBaseId);
	}

    /**
     * Updates infobase parameters
     *
     * @param clusterId cluster ID
     * @param info infobase parameters
     */
    public void updateInfoBase(UUID clusterId, IInfoBaseInfo info)
    {
		if (connection == null)
		{
			throw new IllegalStateException("The connection is not established.");
		}
		
		connection.updateInfoBase(clusterId, info);
    }
    
    public UUID createInfoBase(UUID clusterId, IInfoBaseInfo info, int infobaseCreationMode)
    {
		if (connection == null)
		{
			throw new IllegalStateException("The connection is not established.");
		}
		
		return connection.createInfoBase(clusterId, info, infobaseCreationMode);
    }
    
    public void dropInfoBase(UUID clusterId, UUID infobaseId, int dropMode)
    {
		if (connection == null)
		{
			throw new IllegalStateException("The connection is not established.");
		}
		
		connection.dropInfoBase(clusterId, infobaseId, dropMode);
    }
    
    /**
     * Terminates all sessions for all infobases in the cluster
     *
     * @param clusterId cluster ID
     */
    public void terminateAllSessions(UUID clusterId)
    {
		if (connection == null)
		{
			throw new IllegalStateException("The connection is not established.");
		}

        List<ISessionInfo> sessions = connection.getSessions(clusterId);
        for (ISessionInfo session : sessions)
        {
        	connection.terminateSession(clusterId, session.getSid());
        }
    }
	
    /**
     * Terminates all sessions for infobase in the cluster
     *
     * @param clusterId cluster ID
     * @param infobaseId infobase ID
     */    
    public void terminateAllSessionsOfInfobase(UUID clusterId, UUID infobaseId)
    {
		if (connection == null)
		{
			throw new IllegalStateException("The connection is not established.");
		}

        List<ISessionInfo> sessions = connection.getInfoBaseSessions(clusterId, infobaseId);
        for (ISessionInfo session : sessions)
        {
        	connection.terminateSession(clusterId, session.getSid());
        }
    }	
    
    public void terminateSession(UUID clusterId, UUID sessionId)
    {
		if (connection == null)
		{
			throw new IllegalStateException("The connection is not established.");
		}

        connection.terminateSession(clusterId, sessionId);

    }	

    /**
     * Get sessions for infobase in the cluster
     *
     * @param clusterId cluster ID
     * @param infobaseId infobase ID
     * @return Infobase sessions
     */    
    
    public List<ISessionInfo> getInfoBaseSessions(UUID clusterId, UUID infobaseId)
    {
		if (connection == null)
		{
			throw new IllegalStateException("The connection is not established.");
		}

        return connection.getInfoBaseSessions(clusterId, infobaseId);
        
    }
    
    public List<ISessionInfo> getSessions(UUID clusterId)
    {
		if (connection == null)
		{
			throw new IllegalStateException("The connection is not established.");
		}

        return connection.getSessions(clusterId);
        
    }
    
    public List<IInfoBaseConnectionShort> getConnectionsShort(UUID clusterId)
    {
		if (connection == null)
		{
			throw new IllegalStateException("The connection is not established.");
		}

        return connection.getConnectionsShort(clusterId);
        
    }
    
    public List<IInfoBaseConnectionShort> getInfoBaseConnectionsShort(UUID clusterId, UUID infobaseId)
    {
		if (connection == null)
		{
			throw new IllegalStateException("The connection is not established.");
		}

        return connection.getInfoBaseConnectionsShort(clusterId, infobaseId);
        
    }
    
    public IInfoBaseConnectionShort getConnectionInfoShort(UUID clusterId, UUID connectionId)
    {
		if (connection == null)
		{
			throw new IllegalStateException("The connection is not established.");
		}

        return connection.getConnectionInfoShort(clusterId, connectionId);
        
    }

//    public IInfoBaseConnectionShort terminateConnection(UUID clusterId, UUID connectionId)
//    {
//		if (connection == null)
//		{
//			throw new IllegalStateException("The connection is not established.");
//		}
//
//        return connection.(clusterId, connectionId);
//        
//    }
}
