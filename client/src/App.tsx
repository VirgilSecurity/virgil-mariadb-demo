import React, { useState, useEffect } from 'react';
import Patient from './components/Patient/Patient';
import Physician from './components/Physician/Physician';
import { makeStyles, Theme } from '@material-ui/core/styles';
import { Card, CardContent, Button } from '@material-ui/core';
import StoreContext from './components/StoreContext/StoreContext';
import { IPatient, IPhysician, IPrescription, ICredentials } from './lib/Interfaces';
import { useGlobalStyles } from './lib/styles';
import { ResetReq } from './lib/Connection/Endpoints';
import { Connection } from './lib/Connection/Connection';
import Lab from './components/Lab/Lab';
import { useCount } from './lib/utils';

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
  }
}));

function App() {
  const classes = useStyles();
  const styles = useGlobalStyles();    

  const connection: Connection = new Connection();
  const [patientCred, setPatientCred] = useState<ICredentials | undefined>();
  const [physicianCred, setPhysicianCred] = useState<ICredentials | undefined>();
  const [labCred, setLabCred] = useState<ICredentials | undefined>();

  const [patient, setPatient] = useState<IPatient | undefined>();
  const [physician, setPhysician] = useState<IPhysician | undefined>();
  const [prescription, setPrescription] = useState<IPrescription[] | undefined>();
  const nextPrescriptionId = useCount(Math.floor(Math.random() * Math.floor(100000)));
  const nextTestId = useCount(Math.floor(Math.random() * Math.floor(100000)));
  

  const providerValue = {
    connection,



    patient,
    setPatient,
    physician,
    setPhysician,
    prescription,
    setPrescription,
    nextPrescriptionId,
    nextTestId
  };

  useEffect(() => {
    const pat = localStorage.getItem('patientCred');
    const phy = localStorage.getItem('physicianCred');
    const lab = localStorage.getItem('labCred');
    if (pat) {
      setPatientCred(JSON.parse(pat));
    }
    if (phy) {
      setPhysicianCred(JSON.parse(phy));
    }
    if (lab) {
      setLabCred(JSON.parse(lab));
    }
  }, []);
  
  const handleClear = () => {
    localStorage.clear();
    window.location.reload(false);
  };

  const handleReset = () => {
    localStorage.clear();
    connection.send(new ResetReq()
      .onSuccess((resp) => {
        localStorage.setItem('patientCred', JSON.stringify(resp.patients[1]));
        localStorage.setItem('physicianCred', JSON.stringify(resp.physicians[0]));
        localStorage.setItem('labCred', JSON.stringify(resp.laboratories[0]));
        setPatientCred(resp.patients[1]);
        setPhysicianCred(resp.physicians[0]);
        setLabCred(resp.laboratories[0]);
      })
    );
  };

  return (
    <StoreContext.Provider value={providerValue}>
      <div style={{display: 'flex', justifyContent: 'center', margin: '0 auto'}}>
        <Button
          onClick={handleReset}
          color="primary"
          variant="contained"
          style={{backgroundColor: '#7bbd00', marginRight: '10px'}}
        >Restart demo</Button>
        <Button
          onClick={handleClear}
          color="secondary"
          variant="contained"
        >Clear</Button>
      </div>
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
    </StoreContext.Provider>
  );
};

export default App;
