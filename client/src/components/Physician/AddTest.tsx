import React from 'react';
import { FormControl, InputLabel, Input, Button, makeStyles } from '@material-ui/core';
import { useGlobalStyles } from '../../lib/styles';
import { useSignUpForm } from '../../lib/utils';
import { ILabTest } from '../../lib/Interfaces';
import StoreContext from '../StoreContext/StoreContext';
import {
    MuiPickersUtilsProvider,
    KeyboardDatePicker,
  } from '@material-ui/pickers';
import DateFnsUtils from '@date-io/date-fns';

export interface AddTestProps {
    onSubmit: (inputs:ILabTest) => void; 
};

const useStyles = makeStyles(() => ({
    form: {
        width: '100%',
    },
    input: {
        margin: '15px 0px',
    }
}));

const AddTest:React.FC<AddTestProps> = ({onSubmit}) => {
    const gCss = useGlobalStyles();
    const css = useStyles();
    const { patient, physician, nextTestId } = React.useContext(StoreContext);

    const [selectedDate, setSelectedDate] = React.useState<Date>(new Date(Date.now()));

    const handleDateChange = (date: Date | null) => {
        if (date) {
            setSelectedDate(date);
        }
    };

    const {inputs, handleInputChange, handleSubmit } = useSignUpForm(() => {
        if (patient && physician) {
            const res: ILabTest = {
                id: `test_unique_id_${nextTestId()}`,
                test_name: inputs['text'],
                patient_id: patient.id,
                physician_id: physician.id,
                test_date: selectedDate.toISOString().split('T')[0],
                results: null,
                share: false
            };
            onSubmit(res);
        }
    });

    return (
        <>
            <h3 className={gCss.pageTitle}>New test</h3>
            <form onSubmit={handleSubmit}>
                <FormControl className={css.form}>
                    <InputLabel required htmlFor="text">Name</InputLabel>
                    <Input required name="text" className={css.input} onChange={handleInputChange} id="text" />
                    <MuiPickersUtilsProvider utils={DateFnsUtils}>
                        <KeyboardDatePicker
                            variant="inline"
                            format="yyyy-MM-dd"
                            value={selectedDate}
                            label="Test date"
                            className={css.input}
                            onChange={handleDateChange}
                            autoOk={true}
                            disablePast={true}
                        />
                    </MuiPickersUtilsProvider>

                    <Button color="primary" variant="contained" type="submit">Create</Button>
                </FormControl>
            </form>
        </>
    );
};

export default AddTest;
