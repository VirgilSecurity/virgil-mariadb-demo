import React from 'react';
import { FormControl, Button, makeStyles, TextField } from '@material-ui/core';
import { useGlobalStyles } from '../../lib/styles';
import { useSignUpForm } from '../../lib/utils';

export interface AddResultProps {
    onSubmit: (res: string) => void; 
};

const useStyles = makeStyles(() => ({
    form: {
        width: '100%',
    },
    input: {
        margin: '15px 0px',
    }
}));

const AddResult:React.FC<AddResultProps> = ({onSubmit}) => {
    const gCss = useGlobalStyles();
    const css = useStyles();

    const {inputs, handleInputChange, handleSubmit } = useSignUpForm(() => {
        onSubmit(inputs['text']);
    });

    return (
        <>
            <h3 className={gCss.pageTitle}>Add result</h3>
            <form onSubmit={handleSubmit}>
                <FormControl className={css.form}>
                    <TextField
                        id="text"
                        name="text"
                        label="Result"
                        required
                        multiline
                        className={css.input}
                        onChange={handleInputChange}
                        rows="4"
                        autoFocus={true}
                    />
                    <Button color="primary" variant="contained" type="submit">Submit</Button>
                </FormControl>
            </form>
        </>
    );
};

export default AddResult;
