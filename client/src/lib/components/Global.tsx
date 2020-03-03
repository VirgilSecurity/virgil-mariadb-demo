import React, { useState } from "react";
import { makeStyles } from "@material-ui/core";
import { StyledTableTitle, Ellipsis } from "../styles";

export interface TableTitleProps {
  children: React.ReactNode;
}

export const TableTitle: React.FC<TableTitleProps> = ({ children }) => {
  return <StyledTableTitle>{children}</StyledTableTitle>;
};

const useStyles = makeStyles(() => ({
  root: {
    position: "fixed",
    left: 0,
    right: 0,
    top: 0,
    bottom: 0,
    display: "flex",
    backgroundColor: "#333a",
    zIndex: 5,
    cursor: "pointer"
  },
  container: {
    margin: "auto",
    backgroundColor: "white",
    padding: "10px 20px",
    borderRadius: "2px",
    boxShadow:
      "rgba(0, 0, 0, 0.2) 0px 3px 3px -2px, rgba(0, 0, 0, 0.14) 0px 3px 4px 0px, rgba(0, 0, 0, 0.12) 0px 1px 8px 0px"
  },
  label: {
    fontWeight: 100,
    marginRight: "10px",
    fontSize: "18px"
  },
  result: {
    fontFamily: "Helvetica",
    fontSize: "1em"
  }
}));

export interface TextEllipsisProps {
  children: React.ReactNode;
  title?: string | undefined;
  label?: string;
}

export const TextEllipsis: React.FC<TextEllipsisProps> = ({
  children,
  title,
  label
}) => {
  const css = useStyles();
  const [open, setOpen] = useState(false);

  const handleView = () => {
    setOpen(!open);
  };

  return (
    <>
      <Ellipsis onClick={handleView}>{title || children}</Ellipsis>
      {open && (
        <div onClick={handleView} className={css.root}>
          <div className={css.container}>
            {label && <span className={css.label}>{label}&nbsp;</span>}
            <pre className={css.result}>{children}</pre>
          </div>
        </div>
      )}
    </>
  );
};
