import React, { useState, useEffect } from "react";
import Patient from "./components/Patient/Patient";
import Physician from "./components/Physician/Physician";
import { CardContent } from "@material-ui/core";
import { ICredentials, IReset } from "./lib/Interfaces";
import { ResetReq } from "./lib/Connection/Endpoints";
import { Connection } from "./lib/Connection/Connection";
import Lab from "./components/Lab/Lab";
import { PageTitle, Wrapper, StyledCard, Preloader } from "./lib/styles";
import Nav from "./components/Nav/Nav";
import { debounce } from "./lib/utils";

function App() {
  const connection: Connection = new Connection();
  const [patientCred, setPatientCred] = useState<ICredentials | undefined>();
  const [physicianCred, setPhysicianCred] = useState<
    ICredentials | undefined
  >();
  const [labCred, setLabCred] = useState<ICredentials | undefined>();
  const [isLoading, setLoading] = useState<boolean>(false);

  const initDemo = (init: IReset) => {
    setPatientCred(init.patients[0]);
    setPhysicianCred(init.physicians[0]);
    setLabCred(init.laboratories[0]);
  };

  useEffect(() => {
    const init: IReset | null = JSON.parse(
      sessionStorage.getItem("init") || "{}"
    );
    if (window !== undefined && window.location.pathname !== "/") {
      window.location.pathname = "/";
    }
    if (init?.patients) {
      initDemo(init);
    } else {
      handleReset();
    }
  }, []);

  const handleReset = debounce(() => {
    sessionStorage.clear();
    setLoading(true);
    connection.send(
      new ResetReq().onSuccess(resp => {
        sessionStorage.setItem(
          "init",
          JSON.stringify({ ...resp, isInit: true })
        );
        initDemo(resp);
        setLoading(false);
      })
    );
  }, 500);

  return (
    <>
      <Nav handleReset={handleReset} />
      {isLoading ? (
        <Preloader />
      ) : (
        <Wrapper>
          {patientCred && (
            <StyledCard id="patient">
              <PageTitle>Patient card</PageTitle>
              <CardContent>
                <Patient patientCred={patientCred} />
              </CardContent>
            </StyledCard>
          )}
          {physicianCred && (
            <StyledCard id="physician">
              <PageTitle>Physician card</PageTitle>
              <CardContent>
                <Physician physicianCred={physicianCred} />
              </CardContent>
            </StyledCard>
          )}
          {labCred && (
            <StyledCard id="lab">
              <PageTitle>Lab</PageTitle>
              <CardContent>
                <Lab labCred={labCred} />
              </CardContent>
            </StyledCard>
          )}
        </Wrapper>
      )}
    </>
  );
}

export default App;
