import React, { useState, useEffect } from "react";
import Patient from "./components/Patient/Patient";
import Physician from "./components/Physician/Physician";
import { Card, CardContent, Button } from "@material-ui/core";
import { ICredentials, IReset } from "./lib/Interfaces";
import { ResetReq } from "./lib/Connection/Endpoints";
import { Connection, DB_URL } from "./lib/Connection/Connection";
import Lab from "./components/Lab/Lab";
import styled from "styled-components";
import { PageTitle } from "./lib/styles";

const Wrapper = styled.section`
  margin: 0px;
  width: 100%;
  display: flex;
  flex-direction: row;
  justify-content: center;
  font-family: "Helvetica";
  @media (max-width: 1250px) {
    flex-direction: column;
  }
`;

const StyledCard = styled(Card)`
  padding: 20px 10px;
  margin: 20px 10px;
  background-color: #fffa;
  min-width: 250px;
`;

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

  const handleReset = () => {
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
  };

  return (
    <>
      <div style={{ display: "flex", justifyContent: "center" }}>
        <Button
          onClick={handleReset}
          color="primary"
          variant="contained"
          style={{ backgroundColor: "#7bbd00" }}
        >
          Restart demo
        </Button>
        <Button
          target="_blank"
          color="primary"
          variant="contained"
          href={DB_URL}
          style={{ backgroundColor: "#7bbd00", marginLeft: "10px" }}
        >
          View MariaDB
        </Button>
      </div>
      {isLoading ? (
        <img
          style={{ display: "block", margin: "0 auto" }}
          src="https://flevix.com/wp-content/uploads/2019/07/Ring-Preloader.gif"
        />
      ) : (
        <Wrapper>
          {patientCred && (
            <StyledCard>
              <PageTitle>Patient card</PageTitle>
              <CardContent>
                <Patient patientCred={patientCred} />
              </CardContent>
            </StyledCard>
          )}
          {physicianCred && (
            <StyledCard>
              <PageTitle>Physician card</PageTitle>
              <CardContent>
                <Physician physicianCred={physicianCred} />
              </CardContent>
            </StyledCard>
          )}
          {labCred && (
            <StyledCard>
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
