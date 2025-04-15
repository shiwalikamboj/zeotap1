const testConnection = async (connectionDetails) => {
    try {
      const response = await fetch(`http://localhost:8080/api/transfer/test-connection?` + 
        `host=${connectionDetails.host}&` +
        `port=${connectionDetails.port}&` +
        `database=${connectionDetails.database}&` +
        `user=${connectionDetails.user}&` +
        `token=${connectionDetails.token}`, {
        method: 'GET',
      });
      
      const result = await response.json();
      if (result.status === 'success') {
        // Show success message
        alert('Connection successful!');
      } else {
        // Show error message
        alert('Connection failed: ' + result.message);
      }
    } catch (error) {
      alert('Error testing connection: ' + error.message);
    }
  };
  