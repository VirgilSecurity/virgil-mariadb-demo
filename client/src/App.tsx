import React, { useState, useEffect } from 'react';
import Patient from './components/Patient/Patient';
import Physician from './components/Physician/Physician';
import { makeStyles, Theme } from '@material-ui/core/styles';
import { Card, CardContent, Button } from '@material-ui/core';
import StoreContext from './components/StoreContext/StoreContext';
import { IPatient, IPhysician, IPrescription } from './lib/Interfaces';
import { useGlobalStyles } from './lib/styles';
import { PrescriptionsReq } from './lib/Connection/Endpoints';
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
  const [patient, setPatient] = useState<IPatient | undefined>();
  const [physician, setPhysician] = useState<IPhysician | undefined>();
  const [prescription, setPrescription] = useState<IPrescription[] | undefined>();
  const connection: Connection = new Connection();
  const nextPrescriptionId = useCount(Math.floor(Math.random() * Math.floor(100000)));
  const nextTestId = useCount(Math.floor(Math.random() * Math.floor(100000)));

  const providerValue = {
    patient,
    setPatient,
    physician,
    setPhysician,
    prescription,
    setPrescription,
    connection,
    nextPrescriptionId,
    nextTestId
  };

  useEffect(() => {
    connection.send(new PrescriptionsReq().onSuccess((resp)=>{
        setPrescription(resp);
    }));
  }, []);

  return (
    <StoreContext.Provider value={providerValue}>
      <Button color="primary" variant="contained" style={{display: 'block', backgroundColor: '#7bbd00', margin: '0 auto'}}>Restart demo</Button>
      <div className={classes.root}>
        <Card className={classes.card}>
          <h2 className={styles.pageTitle}>Patient card</h2>
          <CardContent>
            <Patient />
          </CardContent>
        </Card>
        <Card className={classes.card}>
          <h2 className={styles.pageTitle}>Physician card</h2>
          <CardContent>
            <Physician />
          </CardContent>
        </Card>
        <Card className={classes.card}>
          <h2 className={styles.pageTitle}>Lab</h2>
          <CardContent>
            <Lab />
          </CardContent>
        </Card>
      </div>
    </StoreContext.Provider>
  );
};

export default App;
