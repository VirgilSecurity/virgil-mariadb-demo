import React, { useEffect } from 'react';
import { FormControl, InputLabel, Input, Button, makeStyles } from '@material-ui/core';
import { useGlobalStyles } from '../../lib/styles';
import { useSignUpForm } from '../../lib/utils';
import { IPrescriptionPost } from '../../lib/Interfaces';

import {
    MuiPickersUtilsProvider,
    KeyboardDatePicker,
  } from '@material-ui/pickers';
import DateFnsUtils from '@date-io/date-fns';

export interface AddPrescriptionProps {
    onSubmit: (inputs:IPrescriptionPost) => void; 
    patient_id: string;
};

const PLUS_YEAR = 86400000;

const useStyles = makeStyles(() => ({
    form: {
        width: '100%',
    },
    input: {
        margin: '15px 0px',
    }
  }));

const AddPrescription:React.FC<AddPrescriptionProps> = ({onSubmit, patient_id}) => {
    const gCss = useGlobalStyles();
    const css = useStyles();

    const [assignDate, setAssignDate] = React.useState<Date>(new Date(Date.now()));
    const [releaseDate, setReleaseDate] = React.useState<Date>(new Date(assignDate.getTime()+PLUS_YEAR));

    const handleAssignDate = (date: Date | null) => {
        if (date) {
            setAssignDate(date);
        }
    };
    const handleReleaseDate = (date: Date | null) => {
        if (date) {
            setReleaseDate(date);
        }
    };
    
    useEffect(() => {
        if (assignDate.getTime() > releaseDate.getTime()) {
            setReleaseDate(new Date(assignDate.getTime()+PLUS_YEAR));
        }
    }, [assignDate]);

    const {inputs, handleInputChange, handleSubmit} = useSignUpForm(() => {
        if (assignDate < releaseDate) {
            const res: IPrescriptionPost = {
                id: null,
                notes: inputs!['notes'] as string,
                patient_id: patient_id,
                physician_id: null,
                assign_date: assignDate.toISOString().split('T')[0],
                release_date: releaseDate.toISOString().split('T')[0]
            };
            onSubmit(res);
        }
    });

    return (
        <>
            <h3 className={gCss.pageTitle}>New prescription</h3>
            <form onSubmit={handleSubmit}>
                <FormControl className={css.form}>
                    <InputLabel required htmlFor="notes">Notes</InputLabel>
                    <Input required name="notes" className={css.input} onChange={handleInputChange} id="notes" />
                    <MuiPickersUtilsProvider utils={DateFnsUtils}>
                        <KeyboardDatePicker
                            required
                            id="assign-date"
                            variant="inline"
                            format="yyyy-MM-dd"
                            value={assignDate}
                            label="Assign date"
                            className={css.input}
                            onChange={handleAssignDate}
                            autoOk={true}
                            disablePast={true}
                        />
                    </MuiPickersUtilsProvider>
                    <MuiPickersUtilsProvider utils={DateFnsUtils}>
                        <KeyboardDatePicker
                            required
                            id="release-date"
                            variant="inline"
                            format="yyyy-MM-dd"
                            value={releaseDate}
                            label="Release date"
                            className={css.input}
                            onChange={handleReleaseDate}
                            autoOk={true}
                            minDate={new Date(assignDate.getTime()+PLUS_YEAR)}
                        />
                    </MuiPickersUtilsProvider>
                    <Button color="primary" variant="contained" type="submit">Create</Button>
                </FormControl>
            </form>
        </>
    );
};

export default AddPrescription;
