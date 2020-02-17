import React, { ReactNode } from 'react';
import Modal from '@material-ui/core/Modal';
import { Button, Card, makeStyles, Theme, createStyles, CardContent } from '@material-ui/core';

export interface SimpleModalProps {
    children?: ReactNode;
    value: string;
};

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    modal: {
      display: 'flex',
      padding: theme.spacing(1),
      alignItems: 'center',
      justifyContent: 'center',
      fontFamily: 'Helvetica',
    },
    card: {
        minWidth: '400px',
        position: 'relative'
    },
    close: {
      fontWeight: 100,
      fontSize: '28px',
      position: 'absolute',
      left: '94%',
      color: '#aaa',
      cursor: 'pointer',
    }
  }),
);

const SimpleModal:React.FC<SimpleModalProps> = ({ children, value }) => {
  const [open, setOpen] = React.useState(false);
  const classes = useStyles();

  const handleOpen = () => {
    setOpen(true);
  };

  const handleClose = () => {
    setOpen(false);
  };

  return (
    <div>
        <Button onClick={handleOpen} variant="contained" color="primary">{value}</Button>
        <Modal
            aria-labelledby="simple-modal-title"
            aria-describedby="simple-modal-description"
            open={open}
            onClose={handleClose}
            className={classes.modal}
        >
            <Card className={classes.card}>
                <div className={classes.close} onClick={handleClose}>x</div>
                <CardContent>
                    {children}
                </CardContent>
            </Card>
        </Modal>
    </div>
  );
}

export default SimpleModal;