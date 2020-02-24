import React from "react";
import { Button } from "@material-ui/core";
import { DB_URL } from "../../lib/Connection/Connection";
import styled from "styled-components";

export interface NavProps {
  handleReset: () => void;
}

const ButtonContainer = styled.div`
  display: flex;
  justify-content: center;
`;

const Top = styled.div`
  display: none;
  @media (max-width: 928px) {
    display: block;
    height: 70px;
  }
`;

const NavList = styled.ul`
  display: none;
  font-family: "Helvetica";
  justify-content: center;
  box-shadow: 0px 2px 1px -1px rgba(0, 0, 0, 0.2),
    0px 1px 1px 0px rgba(0, 0, 0, 0.14), 0px 1px 3px 0px rgba(0, 0, 0, 0.12);
  @media (max-width: 928px) {
    display: flex;
    position: fixed;
    background: #000;
    top: 0;
    left: 0;
    margin: 0;
    padding: 0;
    width: 100%;
    padding: 10px;
    z-index: 5;
    list-style: none;
  }
`;

const NavItem = styled.li`
  padding: 10px 15px;
`;

const NavLink = styled.a`
  color: #fff;
  text-transform: uppercase;
  text-decoration: none;
`;

const Nav: React.FC<NavProps> = ({ handleReset }) => {
  const handleScroll = (id: string) => {
    const el = document.querySelector(id);
    if (el) {
      window.scrollTo({ top: 0 });
      const rect = el.getBoundingClientRect();
      window.scrollTo({ top: rect.y - 68 });
    }
  };
  return (
    <>
      <Top id="top" />
      <NavList>
        <NavItem>
          <NavLink onClick={() => handleScroll("#top")}>Top</NavLink>
        </NavItem>
        <NavItem>
          <NavLink onClick={() => handleScroll("#patient")}>Patient</NavLink>
        </NavItem>
        <NavItem>
          <NavLink onClick={() => handleScroll("#physician")}>
            Physician
          </NavLink>
        </NavItem>
        <NavItem>
          <NavLink onClick={() => handleScroll("#lab")}>Lab</NavLink>
        </NavItem>
      </NavList>
      <ButtonContainer>
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
      </ButtonContainer>
    </>
  );
};

export default Nav;
