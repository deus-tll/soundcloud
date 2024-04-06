import {useSelector} from "react-redux";
import {selectCurrentToken, selectCurrentUser} from "../services/auth/authSliceService";
import {useEffect, useState} from "react";

const Welcome = () => {
  const user = useSelector(selectCurrentUser);
  const token = useSelector(selectCurrentToken);

  const [welcome, setWelcome] = useState('')
  const [tokenAbbr, setTokenAbbr] = useState('')

  useEffect(() => {
    setWelcome(user ? `Welcome ${user.first_name} ${user.last_name}!` : 'Welcome!');
    setTokenAbbr(`${token.slice(0, 20)}...`);
  }, [user, token]);

  return (
    <section>
      <h1>{welcome}</h1>
      <p>Token: {tokenAbbr}</p>

      {user.avatars && (
        <>
          <div className="d-flex align-items-center justify-center">
            <img src={user.avatars.url_small} alt="avatar"/>
          </div>

          <div className="d-flex align-items-center justify-center">
            <img src={user.avatars.url_medium} alt="avatar"/>
          </div>

          <div className="d-flex align-items-center justify-center">
            <img src={user.avatars.url_large} alt="avatar"/>
          </div>
        </>
      )}
    </section>
  )
}
export default Welcome;