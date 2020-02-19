import React, { useState, useEffect } from 'react';
import Patient from './components/Patient/Patient';
import Physician from './components/Physician/Physician';
import { makeStyles, Theme } from '@material-ui/core/styles';
import { Card, CardContent, Button } from '@material-ui/core';
import { ICredentials, IReset } from './lib/Interfaces';
import { useGlobalStyles } from './lib/styles';
import { ResetReq } from './lib/Connection/Endpoints';
import { Connection, DB_URL } from './lib/Connection/Connection';
import Lab from './components/Lab/Lab';

const useStyles = makeStyles((theme: Theme) => ({
  root: {
    margin: '0px',
    width: '100%',
    display: 'flex',
    flexDirection: 'row',
    justifyContent: 'center',
    fontFamily: 'Helvetica',
  },
  card: {
    padding: '20px 10px',
    margin: '20px 10px',
    backgroundColor: "#FFFa",
    minWidth: '250px'
  },
  loader: {
    border: '16px solid #f3f3f3',
    borderRadius: '50%',
    borderTop: '16px solid #3498db',
    width: '120px',
    height: '120px',
    webkitAnimation: 'spin 2s linear infinite',
    animation: 'spin 2s linear infinite'
  }
}));

function App() {
  const classes = useStyles();
  const styles = useGlobalStyles();    

  const connection: Connection = new Connection();
  const [patientCred, setPatientCred] = useState<ICredentials | undefined>();
  const [physicianCred, setPhysicianCred] = useState<ICredentials | undefined>();
  const [labCred, setLabCred] = useState<ICredentials | undefined>();
  const [isLoading, setLoading] = useState<boolean>(false);

  const initDemo = (init:IReset) => {
    setPatientCred(init.patients[0]);
    setPhysicianCred(init.physicians[0]);
    setLabCred(init.laboratories[0]);
  };

  useEffect(() => {
    const init:IReset | null = JSON.parse(sessionStorage.getItem('init') || '{}');
    if (init?.patients) {
      initDemo(init);
    } else {
      handleReset();
    }
  }, []);

  const handleReset = () => {
    sessionStorage.clear();
    setLoading(true);
    connection.send(new ResetReq()
    .onSuccess((resp) => {
      sessionStorage.setItem('init', JSON.stringify({...resp, isInit: true}));
      initDemo(resp);
      setLoading(false);
    })
    .onError((err) => {console.log(err)})
    );
  };
  
  return (
    <>
      <div 
          style={{display: 'flex', justifyContent: 'center'}}
      >
        <Button
          onClick={handleReset}
          color="primary"
          variant="contained"
          style={{backgroundColor: '#7bbd00'}}
          >Restart demo</Button>
        <Button
          target='_blank'
          color="primary"
          variant="contained"
          href={DB_URL}
          style={{backgroundColor: '#7bbd00', marginLeft: '10px'}}
          >View MariaDB</Button>
        </div>
      {isLoading ? 
        <img style={{display: 'block', margin: '0 auto'}} src="https://flevix.com/wp-content/uploads/2019/07/Ring-Preloader.gif"/>
        :
        <div className={classes.root}>
          {patientCred && <Card className={classes.card}>
            <h2 className={styles.pageTitle}>Patient card</h2>
            <CardContent>
              <Patient patientCred={patientCred}/> 
            </CardContent>
          </Card>}
          {physicianCred && <Card className={classes.card}>
            <h2 className={styles.pageTitle}>Physician card</h2>
            <CardContent>
              <Physician physicianCred={physicianCred}/>
            </CardContent>
          </Card>}
          {labCred && <Card className={classes.card}>
            <h2 className={styles.pageTitle}>Lab</h2>
            <CardContent>
              <Lab labCred={labCred}/>
            </CardContent>
          </Card>}
        </div>
      }
    </>
  );
};

export default App;
